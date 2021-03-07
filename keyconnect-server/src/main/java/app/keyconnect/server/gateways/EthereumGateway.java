package app.keyconnect.server.gateways;

import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.api.client.model.BlockchainAccountPaymentItem;
import app.keyconnect.api.client.model.BlockchainAccountPayments;
import app.keyconnect.api.client.model.BlockchainAccountTransaction;
import app.keyconnect.api.client.model.BlockchainAccountTransactionItem;
import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.api.client.model.BlockchainFee;
import app.keyconnect.api.client.model.BlockchainNetworkServerStatus;
import app.keyconnect.api.client.model.BlockchainNetworkServerStatus.StatusEnum;
import app.keyconnect.api.client.model.CurrencyValue;
import app.keyconnect.api.client.model.CurrencyValue.CurrencyEnum;
import app.keyconnect.api.client.model.SubAccountInfo;
import app.keyconnect.api.client.model.SubmitTransactionRequest;
import app.keyconnect.api.client.model.SubmitTransactionResult;
import app.keyconnect.server.factories.configuration.BlockchainNetworkConfiguration;
import app.keyconnect.server.gateways.exceptions.EthTransactionsCursorMustBePageNumberException;
import app.keyconnect.server.gateways.exceptions.FailedToSubmitEthTransactionException;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.server.gateways.exceptions.UnsupportedNetworkForEthTransactionsException;
import app.keyconnect.server.services.Erc20TokenService;
import app.keyconnect.server.services.networks.NetworkClient;
import app.keyconnect.server.services.networks.NetworkClientService;
import app.keyconnect.server.utils.EtherscanUtil;
import app.keyconnect.server.utils.models.EtherscanResponse;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.exceptions.MessageDecodingException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

public class EthereumGateway implements
    BlockchainGateway {

  public static final BigDecimal ETH_SCALE = BigDecimal.valueOf(1000000000000000000L);
  private static final Logger logger = LoggerFactory.getLogger(EthereumGateway.class);
  public static final String CHAIN_ID = "eth";
  public static final int SCALE = 18;
  public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  private static final String DEFAULT_NETWORK = "mainnet";
  public static final String TX_STATUS_OK = "ok";
  public static final String TX_STATUS_UNKNOWN = "unknown";
  private final LoadingCache<String, EthBlock> latestEthBlockCache;
  private final EtherscanUtil etherscanUtil;
  private final Erc20TokenService tokenService;
  private final NetworkClientService<Web3j> networkClientService;

  public EthereumGateway(
      EtherscanUtil etherscanUtil,
      Erc20TokenService tokenService,
      NetworkClientService<Web3j> networkClientService) {
    this.etherscanUtil = etherscanUtil;
    this.tokenService = tokenService;
    this.networkClientService = networkClientService;

    this.latestEthBlockCache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .build(new CacheLoader<>() {
          @Override
          public EthBlock load(@NotNull String serverUrl) throws Exception {
            final Web3j client = EthereumGateway.this.networkClientService.getClientForServer(serverUrl);
            return client.ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), false)
                .sendAsync().get(30, TimeUnit.SECONDS);
          }
        });
  }

  @Override
  public String getChainId() {
    return CHAIN_ID;
  }

  @Override
  @Cacheable("elephant")
  public String[] getNetworks() {
    return networkClientService.getNetworks()
        .stream()
        .map(BlockchainNetworkConfiguration::getGroup)
        .distinct()
        .toArray(String[]::new);
  }

  @Override
  public String validateNetworkOrDefault(String network) throws UnknownNetworkException {
    if (Strings.isNullOrEmpty(network)) return DEFAULT_NETWORK;

    if (networkClientService.getNetworks()
        .stream()
        .anyMatch(n -> n.getGroup().equalsIgnoreCase(network))
    ) return network;

    throw new UnknownNetworkException(CHAIN_ID, network);
  }

  @Override
  @Cacheable("fast")
  public BlockchainNetworkServerStatus[] getNetworkServerStatus(String network) {
    return networkClientService.getAllMatching(network)
        .stream()
        .map(n -> {
          final String serverUrl = n.getNetwork().getAddress();
          try {
            final EthBlock latestEthBlock = n.getClient()
                .ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), false)
                .sendAsync().get(30, TimeUnit.SECONDS);
            return new BlockchainNetworkServerStatus()
                .status(latestEthBlock.getBlock().getHash() != null ? StatusEnum.HEALTHY
                    : StatusEnum.UNHEALTHY)
                .host(toHost(serverUrl))
                .lastCheck(Instant.now().toString());
          } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.warn("Unable to get latest block to obtain status for network=" + n, e);
            return new BlockchainNetworkServerStatus()
                .status(StatusEnum.UNHEALTHY)
                .host(toHost(serverUrl))
                .lastCheck(Instant.now().toString());
          }
        })
        .toArray(BlockchainNetworkServerStatus[]::new);
  }

  @Override
  @Cacheable(value = "fast")
  public BlockchainFee getFee(String network) {
    logger.info("Getting fee for {}", network);
    final Set<NetworkClient<Web3j>> networkClients = networkClientService.getAllMatching(network);

    for (NetworkClient<Web3j> networkClient : networkClients) {
      final String serverUrl = networkClient.getNetwork().getAddress();
      try {
        final Web3j client = networkClient.getClient();
        final EthGasPrice gasPrice = client.ethGasPrice().sendAsync().get(30, TimeUnit.SECONDS);
        return new BlockchainFee()
            .server(toHost(serverUrl))
            .network(network)
            .chainId(BlockchainFee.ChainIdEnum.ETH)
            .fee(
                new CurrencyValue()
                    .currency(CurrencyEnum.GAS)
                    .amount(gasPrice.getGasPrice().toString())
            );
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        logger.warn("Unable to get eth.fee, network=" + network, e);
      }
    }

    return null;
  }

  @Override
  @Cacheable(value = "slow")
  public BlockchainAccountInfo getAccount(String network, String accountId)
      throws UnknownNetworkException {
    final Set<NetworkClient<Web3j>> networkClients = networkClientService.getAllMatching(network);
    if (networkClients.size() == 0) {
      // we could not find the specified network
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    final BlockchainAccountInfo accountInfo = new BlockchainAccountInfo()
        .chainId(ChainIdEnum.ETH)
        .accountId(accountId)
        .network(network);

    for (NetworkClient<Web3j> networkClient : networkClients) {
      final String serverUrl = networkClient.getNetwork().getAddress();
      final Web3j client = networkClient.getClient();
      try {
        final EthGetBalance balance = client
            .ethGetBalance(accountId, DefaultBlockParameter.valueOf("latest")).sendAsync()
            .get(30, TimeUnit.SECONDS);

        final Block block = latestEthBlockCache.get(serverUrl).getBlock();
        final BigInteger latestBlock = block.getNumber();
        final BigInteger balanceNum = balance.getBalance();
        final BigDecimal ethBalance = toEthDecimal(balanceNum);
        final EthGetTransactionCount ethGetTransactionCount = client
            .ethGetTransactionCount(accountId, DefaultBlockParameterName.PENDING).sendAsync().get();
        final BigInteger txCount;
        if (ethGetTransactionCount == null || ethGetTransactionCount.getTransactionCount() == null) {
          txCount = BigInteger.ZERO;
        } else {
          txCount = ethGetTransactionCount.getTransactionCount();
        }

        final String nonceVal = txCount.toString();

        final List<SubAccountInfo> subAccountInfo = BigInteger.ZERO.equals(txCount)
            ? Collections.emptyList()
            : tokenService.getAllSubAccountInfo(network, accountId,latestBlock);
        return accountInfo
            .server(toHost(serverUrl))
            .balance(
                new CurrencyValue()
                    .amount(
                        ethBalance
                            .toString()
                    )
                    .currency(CurrencyEnum.ETH)
            )
            .nonce(nonceVal)
            .subAccounts(subAccountInfo);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        logger.warn("Unable to get eth.accountInfo, network=" + network, e);
      } catch (MessageDecodingException e ) {
        if (e.getMessage().contains("Value must be in format")) {
          logger.info("Specified address is not a valid ethereum address, specified={}", accountId);
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested account " + accountId + " is invalid on " + CHAIN_ID + " blockchain");
        }
        logger.warn("Error decoding message from the ethereum blockchain", e);
      }
    }

    // usually never gets here for new accounts because eth does not have activation fee like xrp
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested account " + accountId + " was not found on " + CHAIN_ID + " " + network);
  }

  @NotNull
  private BigDecimal toEthDecimal(BigInteger balanceNum) {
    return new BigDecimal(balanceNum)
        .divide(ETH_SCALE, SCALE, ROUNDING_MODE);
  }

  @Override
  public void fundAccount(String network, String accountId) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "API not yet available on ethereum gateway");
  }

  // https://stackoverflow.com/a/5439547
  private boolean isInteger(String s) {
    return isInteger(s, 10);
  }

  private boolean isInteger(String s, int radix) {
    if (s.isEmpty()) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      if (i == 0 && s.charAt(i) == '-') {
        if (s.length() == 1) {
          return false;
        } else {
          continue;
        }
      }
      if (Character.digit(s.charAt(i), radix) < 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  @Cacheable(value = "slow")
  public BlockchainAccountTransactions getTransactions(String accountId, String network, int limit,
      String cursor) throws UnknownNetworkException {
    if (!"mainnet".equalsIgnoreCase(network)) {
      throw new UnsupportedNetworkForEthTransactionsException();
    }
    if (!(Strings.isNullOrEmpty(cursor) || "null".equalsIgnoreCase(cursor)) && !isInteger(cursor)) {
      throw new EthTransactionsCursorMustBePageNumberException();
    }

    final String pageNumber = Strings.isNullOrEmpty(cursor) || "null".equalsIgnoreCase(cursor) ? "1" : cursor;
    final int iCursor = Integer.parseInt(pageNumber);

    final Set<NetworkClient<Web3j>> networkClients = networkClientService.getAllMatching(network);

    List<BlockchainAccountTransactionItem> transactionItems = null;
    for (NetworkClient<Web3j> networkClient : networkClients) {
      final String serverUrl = networkClient.getNetwork().getAddress();
      final String latestBlockNumber;
      try {
        latestBlockNumber = latestEthBlockCache.get(serverUrl).getBlock().getNumber()
            .toString();
      } catch (ExecutionException e) {
        logger.warn("Unable to get latest block to get all transactions, account=" + accountId
            + ", network=" + network, e);
        continue;
      }

      final EtherscanResponse transactions = etherscanUtil
          .getTransactionsForAccount(network, accountId,
              latestBlockNumber, pageNumber, String.valueOf(limit));
      transactionItems = Arrays.stream(transactions.getResult())
          .map(t -> {
            final BigDecimal amountInEth = new BigDecimal(t.getValue())
                .divide(ETH_SCALE, SCALE, ROUNDING_MODE);
            return new BlockchainAccountTransactionItem()
                .amount(
                    new CurrencyValue()
                        .amount(amountInEth.toString())
                        .currency(CurrencyEnum.ETH)
                )
                .sourceAccount(t.getFrom())
                .destinationAccount(t.getTo())
                .fee(
                    new CurrencyValue()
                        .amount(t.getGas())
                        .currency(CurrencyEnum.GAS)
                )
                .type("transaction")
                .hash(t.getHash())
                .status(TX_STATUS_OK);
          })
          .collect(Collectors.toList());
    }

    return new BlockchainAccountTransactions()
        .accountId(accountId)
        .network(network)
        .server("https://api.etherscan.io")
        .chainId(BlockchainAccountTransactions.ChainIdEnum.ETH)
        .cursor(String.valueOf(iCursor + 1))
        .transactions(
            transactionItems
        );
  }

  @Override
  @Cacheable(value = "slow")
  public BlockchainAccountPayments getPayments(String accountId, String network, int limit,
      String cursor) throws UnknownNetworkException {
    BlockchainAccountTransactions blockchainAccountTransactions = getTransactions(accountId,
        network, limit,
        cursor);
    List<BlockchainAccountTransactionItem> transactions = blockchainAccountTransactions
        .getTransactions() != null ? blockchainAccountTransactions.getTransactions()
        : new ArrayList<>(0);

    return new BlockchainAccountPayments()
        .accountId(accountId)
        .network(network)
        .server(blockchainAccountTransactions.getServer())
        .chainId(BlockchainAccountPayments.ChainIdEnum.ETH)
        .cursor(blockchainAccountTransactions.getCursor())
        .payments(
            transactions
                .stream()
                .filter(t -> "transaction".equalsIgnoreCase(t.getType()))
                .map(t -> new BlockchainAccountPaymentItem()
                    .amount(t.getAmount())
                    .fee(t.getFee())
                    .sourceAccount(t.getSourceAccount())
                    .destinationAccount(t.getDestinationAccount())
                    .destinationTag(t.getDestinationTag())
                    .hash(t.getHash())
                    .status(t.getStatus()))
                .collect(Collectors.toList())
        );
  }

  @Override
  @Caching(
      cacheable = {
          @Cacheable(value = "elephant", unless = "#result.transaction == null || #result.transaction.status.equalsIgnoreCase('ok') == false"),
          @Cacheable(value = "slow", unless = "#result.transaction == null || #result.transaction.status.equalsIgnoreCase('ok') == true")
      }
  )
  public BlockchainAccountTransaction getTransaction(String network, String hash)
      throws UnknownNetworkException {
    final Set<NetworkClient<Web3j>> networkClients = networkClientService.getAllMatching(network);

    for (NetworkClient<Web3j> networkClient : networkClients) {
      final String serverUrl = networkClient.getNetwork().getAddress();
      final Web3j client = networkClient.getClient();
      try {
        final EthTransaction ethTransaction = client.ethGetTransactionByHash(hash).sendAsync()
            .get(30, TimeUnit.SECONDS);
        final Transaction transaction = ethTransaction.getTransaction().get();
        final BigDecimal amountInEth = toEthDecimal(transaction.getValue());
        return new BlockchainAccountTransaction()
            .network(network)
            .server(toHost(serverUrl))
            .chainId(BlockchainAccountTransaction.ChainIdEnum.ETH)
            .transaction(
                new BlockchainAccountTransactionItem()
                    .amount(
                        new CurrencyValue()
                            .amount(amountInEth.toString())
                            .currency(CurrencyEnum.ETH)
                    )
                    .sourceAccount(transaction.getFrom())
                    .destinationAccount(transaction.getTo())
                    .fee(
                        new CurrencyValue()
                            .amount(transaction.getGas().toString())
                            .currency(CurrencyEnum.GAS)
                    )
                    .type("transaction")
                    .hash(transaction.getHash())
                    .status(determineStatus(serverUrl, transaction.getBlockNumber()))
            );
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        logger.warn("Unable to get eth.getTransaction, network=" + network + ", hash=" + hash, e);
      }
    }
    return null;
  }

  private String toHost(String url) {
    try {
      return new URI(url).getHost();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  private String determineStatus(String serverUrl, BigInteger transactionBlockNumber)
      throws ExecutionException {
    final EthBlock latestBlock = latestEthBlockCache.get(serverUrl);
    final int comparedToLatestBlock = latestBlock.getBlock().getNumber()
        .compareTo(transactionBlockNumber);
    if (comparedToLatestBlock > 0) {
      return TX_STATUS_OK;
    } else {
      return TX_STATUS_UNKNOWN;
    }
  }

  @Override
  public SubmitTransactionResult submitTransaction(String network,
      SubmitTransactionRequest submitTransactionRequest) throws UnknownNetworkException {
    final Set<NetworkClient<Web3j>> networkClients = networkClientService.getAllMatching(network);
    for (NetworkClient<Web3j> networkClient : networkClients) {
      final String serverUrl = networkClient.getNetwork().getAddress();
      final Web3j client = networkClient.getClient();
      final EthSendTransaction response;
      try {
        response = client
            .ethSendRawTransaction(submitTransactionRequest.getTransaction()).sendAsync()
            .get(30, TimeUnit.SECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        logger.warn("Unable to send transaction network=" + network, e);
        continue;
      }

      if (response.hasError()) {
        // handle other errors...
        if (response.getError().getMessage().contains("exceeds block gas limit")) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specified gas limit is too high");
        }
      }

      return new SubmitTransactionResult()
          .server(serverUrl)
          .transaction(
              new BlockchainAccountTransactionItem()
                .hash(response.getTransactionHash())
          )
          .network(network)
          .chainId(SubmitTransactionResult.ChainIdEnum.ETH);
    }
    throw new FailedToSubmitEthTransactionException();
  }
}
