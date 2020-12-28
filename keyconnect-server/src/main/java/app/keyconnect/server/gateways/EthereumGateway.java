package app.keyconnect.server.gateways;

import app.keyconnect.server.factories.configuration.BlockchainNetworkConfiguration;
import app.keyconnect.server.factories.configuration.BlockchainsConfiguration;
import app.keyconnect.server.factories.configuration.YamlConfiguration;
import app.keyconnect.server.gateways.exceptions.EthTransactionsCursorMustBePageNumberException;
import app.keyconnect.server.gateways.exceptions.FailedToSubmitEthTransactionException;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.server.gateways.exceptions.UnsupportedNetworkForEthTransactionsException;
import app.keyconnect.server.utils.EtherscanUtil;
import app.keyconnect.server.utils.models.EtherscanResponse;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import app.keyconnect.api.client.model.SubmitTransactionRequest;
import app.keyconnect.api.client.model.SubmitTransactionResult;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;

public class EthereumGateway implements
    BlockchainGateway {

  private static final BigDecimal ETH_SCALE = BigDecimal.valueOf(1000000000000000000L);
  private static final Logger logger = LoggerFactory.getLogger(EthereumGateway.class);
  public static final String CHAIN_ID = "eth";
  public static final int SCALE = 18;
  public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  private final BlockchainsConfiguration configuration;
  // cache key is network
  private final Map<String, Web3j> serverClients;
  private final LoadingCache<String, EthBlock> latestEthBlockCache;
  private EtherscanUtil etherscanUtil;

  public EthereumGateway(
      YamlConfiguration configuration,
      EtherscanUtil etherscanUtil) {
    this.configuration = configuration.getBlockchains()
        .stream()
        .filter(b -> b.getType().equalsIgnoreCase(CHAIN_ID))
        .findFirst()
        .orElse(new BlockchainsConfiguration());
    this.etherscanUtil = etherscanUtil;

    // pre populate server clients cache
    this.serverClients = new ConcurrentHashMap<>(this.configuration.getNetworks().size());
    this.configuration.getNetworks()
        .stream()
        .map(BlockchainNetworkConfiguration::getAddress)
        .distinct()
        .forEach(a -> {
          final Web3j client = Web3j.build(new HttpService(a));
          this.serverClients.put(a, client);
        });
    this.latestEthBlockCache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .build(new CacheLoader<>() {
          @Override
          public EthBlock load(String network) throws Exception {
            final Web3j client = serverClients.get(network);
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
  public String[] getNetworks() {
    return configuration.getNetworks()
        .stream()
        .map(BlockchainNetworkConfiguration::getGroup)
        .distinct()
        .toArray(String[]::new);
  }

  @Override
  public BlockchainNetworkServerStatus[] getNetworkServerStatus(String network) {
    final List<BlockchainNetworkConfiguration> eligibleNetworks = this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());

    return eligibleNetworks.stream()
        .map(n -> {
          final String serverUrl = n.getAddress();
          final Web3j client = serverClients.get(serverUrl);
          try {
            final EthBlock latestEthBlock = client
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
  public BlockchainFee getFee(String network) {
    final List<BlockchainNetworkConfiguration> eligibleNetworks = this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());

    for (BlockchainNetworkConfiguration eligibleNetwork : eligibleNetworks) {
      final String serverUrl = eligibleNetwork.getAddress();
      try {
        final Web3j client = serverClients.get(serverUrl);
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
  public BlockchainAccountInfo getAccount(String network, String accountId)
      throws UnknownNetworkException {
    final List<BlockchainNetworkConfiguration> eligibleNetworks = this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());
    if (eligibleNetworks.size() == 0) {
      // we could not find the specified network
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    final BlockchainAccountInfo accountInfo = new BlockchainAccountInfo()
        .chainId(ChainIdEnum.ETH)
        .accountId(accountId)
        .network(network);

    for (BlockchainNetworkConfiguration eligibleNetwork : eligibleNetworks) {
      final String serverUrl = eligibleNetwork.getAddress();
      final Web3j client = serverClients.get(serverUrl);
      try {
        final EthGetBalance balance = client
            .ethGetBalance(accountId, DefaultBlockParameter.valueOf("latest")).sendAsync()
            .get(30, TimeUnit.SECONDS);

        return accountInfo
            .server(toHost(serverUrl))
            .balance(
                new CurrencyValue()
                    .amount(
                        new BigDecimal(balance.getBalance())
                            .divide(ETH_SCALE, SCALE, ROUNDING_MODE)
                            .toString()
                    )
                    .currency(CurrencyEnum.ETH)
            );
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        logger.warn("Unable to get eth.accountInfo, network=" + eligibleNetwork, e);
      }
    }
    // todo pending lastTransactionId
    return accountInfo;
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

    final List<BlockchainNetworkConfiguration> eligibleNetworks = this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());

    List<BlockchainAccountTransactionItem> transactionItems = null;
    for (BlockchainNetworkConfiguration eligibleNetwork : eligibleNetworks) {
      final String serverUrl = eligibleNetwork.getAddress();
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
                .divide(ETH_SCALE, SCALE, RoundingMode.DOWN);
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
                .status("ok");
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
  public BlockchainAccountTransaction getTransaction(String network, String hash)
      throws UnknownNetworkException {
    final List<BlockchainNetworkConfiguration> eligibleNetworks = this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());
    for (BlockchainNetworkConfiguration eligibleNetwork : eligibleNetworks) {
      final String serverUrl = eligibleNetwork.getAddress();
      final Web3j client = serverClients.get(serverUrl);
      try {
        final EthTransaction ethTransaction = client.ethGetTransactionByHash(hash).sendAsync()
            .get(30, TimeUnit.SECONDS);
        final Transaction transaction = ethTransaction.getTransaction().get();
        final BigDecimal amountInEth = new BigDecimal(transaction.getValue())
            .divide(ETH_SCALE, SCALE, RoundingMode.DOWN);
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
      return "ok";
    } else {
      return "unknown";
    }
  }

  @Override
  public SubmitTransactionResult submitTransaction(String network,
      SubmitTransactionRequest submitTransactionRequest) throws UnknownNetworkException {
    final List<BlockchainNetworkConfiguration> eligibleNetworks = this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());
    for (BlockchainNetworkConfiguration eligibleNetwork : eligibleNetworks) {
      final String serverUrl = eligibleNetwork.getAddress();
      final Web3j client = serverClients.get(serverUrl);
      final EthSendTransaction response;
      try {
        response = client
            .ethSendRawTransaction(submitTransactionRequest.getTransaction()).sendAsync()
            .get(30, TimeUnit.SECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        logger.warn("Unable to send transaction network=" + network, e);
        continue;
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
