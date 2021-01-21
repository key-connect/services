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
import app.keyconnect.api.client.model.SubmitTransactionRequest;
import app.keyconnect.api.client.model.SubmitTransactionResult;
import app.keyconnect.rippled.api.client.PublicRippledClient;
import app.keyconnect.rippled.api.client.model.AccountInfoResponse;
import app.keyconnect.rippled.api.client.model.AccountTransaction;
import app.keyconnect.rippled.api.client.model.AccountTransactionItem;
import app.keyconnect.rippled.api.client.model.AccountTransactionMarker;
import app.keyconnect.rippled.api.client.model.AccountTransactionResponse;
import app.keyconnect.rippled.api.client.model.FeeResponse;
import app.keyconnect.rippled.api.client.model.ServerInfoResponse;
import app.keyconnect.rippled.api.client.model.SubmitTransactionResponse;
import app.keyconnect.rippled.api.client.model.TransactionResponse;
import app.keyconnect.rippled.api.client.model.TransactionResult;
import app.keyconnect.server.controllers.exceptions.InvalidCursorException;
import app.keyconnect.server.factories.configuration.BlockchainNetworkConfiguration;
import app.keyconnect.server.factories.configuration.BlockchainsConfiguration;
import app.keyconnect.server.factories.configuration.YamlConfiguration;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.server.services.networks.NetworkClientService;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XrpGateway implements BlockchainGateway {

  private static final Logger logger = LoggerFactory.getLogger(XrpGateway.class);
  public static final String CHAIN_ID = "xrp";
  public static final BigDecimal DROPS_PER_XRP = BigDecimal.valueOf(100000);
  private static final Duration SERVER_INFO_CACHE_EXPIRY = Duration.of(30, ChronoUnit.SECONDS);
  private static final String DEFAULT_NETWORK = "mainnet";
  private final BlockchainsConfiguration configuration;
  private final NetworkClientService<PublicRippledClient> networkClientService;
  // key is serverUrl
  private final LoadingCache<String, ServerInfoResponse> serverInfoCache;
  // key is serverUrl
  private final LoadingCache<String, FeeResponse> networkFeeCache;
  // key is in form of <serverUrl>|<address>
  private final LoadingCache<String, AccountInfoResponse> walletAccountInfoCache;

  //  private final Environment environment;

  public XrpGateway(YamlConfiguration configuration, NetworkClientService<PublicRippledClient> networkClientService) {
    // assert configuration and networks are non null
    // populate server clients
    // for simplicity we get only configure the first xrp configuration
    this.configuration = configuration.getBlockchains()
        .stream()
        .filter(b -> b.getType().equalsIgnoreCase(CHAIN_ID))
        .findFirst()
        .get();
    this.networkClientService = networkClientService;

    serverInfoCache = CacheBuilder.newBuilder()
        .expireAfterWrite(SERVER_INFO_CACHE_EXPIRY)
        .build(new CacheLoader<>() {
          @Override
          public ServerInfoResponse load(@NotNull String key) throws Exception {
            return networkClientService.getClientForServer(key).getServerInfo();
          }
        });

    walletAccountInfoCache = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.of(30, ChronoUnit.SECONDS))
        .build(new CacheLoader<>() {
          @Override
          public AccountInfoResponse load(@NotNull String key) throws Exception {
            final String[] tokens = key.split("\\|");
            final String serverUrl = tokens[0];
            final String address = tokens[1];
            return networkClientService.getClientForServer(serverUrl).getAccountInfo(address);
          }
        });

    networkFeeCache = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.of(1, ChronoUnit.MINUTES))
        .build(new CacheLoader<>() {
          @Override
          public FeeResponse load(@NotNull String key) throws Exception {
            return networkClientService.getClientForServer(key).getFee();
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
  public String validateNetworkOrDefault(String network) throws UnknownNetworkException {
    if (Strings.isNullOrEmpty(network)) return DEFAULT_NETWORK;

    if (configuration.getNetworks()
        .stream()
        .anyMatch(n -> n.getGroup().equalsIgnoreCase(network))
    ) return network;

    throw new UnknownNetworkException(CHAIN_ID, network);
  }

  @Override
  public BlockchainNetworkServerStatus[] getNetworkServerStatus(String network)
      throws UnknownNetworkException {
    final List<BlockchainNetworkConfiguration> foundNetworks = configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());
    if (foundNetworks.size() == 0) {
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    return foundNetworks.stream()
        .map(c -> {
          try {
            final ServerInfoResponse serverInfoResponse = serverInfoCache.get(c.getAddress());
            // todo refine the way the BlockchainNetworkStatus is constructed
            return new BlockchainNetworkServerStatus()
                // todo be defensive, this is relying too much on rippled behaving correctly
                .status(serverInfoResponse.getResult().getStatus().equalsIgnoreCase("success")
                    ? StatusEnum.HEALTHY : StatusEnum.UNHEALTHY)
                .host(toURI(c))
                // todo either remove lastCheck or find more reliable way to get this
                .lastCheck(Instant.now().toString());
          } catch (Throwable e) {
            logger.warn("Unable to get serverInfo to obtain status for network=" + c, e);
            return new BlockchainNetworkServerStatus()
                .status(StatusEnum.UNHEALTHY)
                .host(toURI(c))
                .lastCheck(Instant.now().toString());
          }
        })
        .toArray(BlockchainNetworkServerStatus[]::new);
  }

  public BlockchainFee getFee(String specifiedNetwork) throws UnknownNetworkException {
    final String network = validateNetworkOrDefault(specifiedNetwork);
    final List<BlockchainNetworkConfiguration> eligibleNetworks = configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        // there's a better way to do this
        .collect(Collectors.toList());
    if (eligibleNetworks.size() == 0) {
      // we could not find the specified network
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    for (BlockchainNetworkConfiguration eligibleNetwork : eligibleNetworks) {
      final String serverUrl = eligibleNetwork.getAddress();
      try {
        final FeeResponse feeResponse = networkFeeCache.get(serverUrl);
        final CurrencyValue fee = new CurrencyValue()
            .amount(feeResponse.getResult().getDrops().getMinimumFee())
            .currency(CurrencyEnum.DROPS);
        return new BlockchainFee()
            .chainId(BlockchainFee.ChainIdEnum.XRP)
            .fee(fee)
            .network(network)
            .server(serverUrl);
      } catch (ExecutionException e) {
        logger.warn("Unable to get xrp.fee, network=" + network, e);
      }
    }

    // todo do something if its null
    return null;
  }

  @Override
  public BlockchainAccountInfo getAccount(String network, String accountId)
      throws UnknownNetworkException {
    final List<BlockchainNetworkConfiguration> eligibleNetworks = configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        // there's a better way to do this
        .collect(Collectors.toList());
    if (eligibleNetworks.size() == 0) {
      // we could not find the specified network
      throw new UnknownNetworkException(CHAIN_ID, network);
    }
    // for mainnet xrp there would be two of these
    // we do this to allow fallback
    AccountInfoResponse accountInfoResponse = null;
    BlockchainNetworkConfiguration selectedNetwork = null;
    for (BlockchainNetworkConfiguration eligibleNetwork : eligibleNetworks) {
      final String networkAddress = eligibleNetwork.getAddress();
      final String key = networkAddress + "|" + accountId;
      try {
        accountInfoResponse = walletAccountInfoCache.get(key);
        selectedNetwork = eligibleNetwork;
        break;
      } catch (Throwable e) {
        // swallow all exceptions


      }
    }
    final BlockchainAccountInfo accountInfo = new BlockchainAccountInfo()
        .chainId(ChainIdEnum.XRP)
        .accountId(accountId)
        .network(network);

    if (selectedNetwork != null) {
      accountInfo.setServer(selectedNetwork.getAddress());
    }

    if (accountInfoResponse != null
        && accountInfoResponse.getResult() != null
        && accountInfoResponse.getResult().getAccountData() != null) {
      if (accountInfoResponse.getResult().getAccountData().getBalance() != null) {
        final BigDecimal balanceInXrp = new BigDecimal(
            accountInfoResponse.getResult().getAccountData().getBalance())
            .divide(DROPS_PER_XRP, RoundingMode.DOWN);

        accountInfo.setBalance(
            new CurrencyValue()
                .amount(balanceInXrp.toPlainString())
                .currency(CurrencyEnum.XRP)
        );
      }

      accountInfo.setLastTransactionId(
          accountInfoResponse.getResult().getAccountData().getPreviousTxnID());
    }

    return accountInfo;
  }

  @Override
  public BlockchainAccountTransactions getTransactions(String accountId, String network,
      int limit, String cursor)
      throws UnknownNetworkException {
    final List<BlockchainNetworkConfiguration> selectedNetworks = configuration.getNetworks()
        .stream()
        .filter(c -> c.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());

    if (selectedNetworks.size() == 0) {
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    final PublicRippledClient client = networkClientService.getClientForServer(selectedNetworks.get(0).getAddress());
    AccountTransactionMarker requestMarker = null;
    if (StringUtils.isNotBlank(cursor) && cursor.contains(":")) {
      final String[] markerLedgerAndSeq = cursor.split(":");
      try {
        requestMarker = new AccountTransactionMarker()
            .ledger(Integer.valueOf(markerLedgerAndSeq[0]))
            .seq(Integer.valueOf(markerLedgerAndSeq[1]));
      } catch (NumberFormatException e) {
        throw new InvalidCursorException();
      }
    }
    final AccountTransactionResponse accountTransactionsResponse = client
        .getAccountTransactions(accountId, limit, requestMarker);
    final List<AccountTransactionItem> transactions = accountTransactionsResponse.getResult()
        .getTransactions();

    final AccountTransactionMarker responseMarker = accountTransactionsResponse.getResult()
        .getMarker();
    if (responseMarker != null) {
      cursor = responseMarker.getLedger() + ":" + responseMarker.getSeq();
    } else {
      cursor = null;
    }

    return new BlockchainAccountTransactions()
        .accountId(accountId)
        .network(network)
        .server(selectedNetworks.get(0).getAddress())
        .chainId(BlockchainAccountTransactions.ChainIdEnum.XRP)
        .cursor(cursor)
        .transactions(
            transactions.stream()
                .map(t -> {
                  final AccountTransaction tx = t.getTx();
                  return new BlockchainAccountTransactionItem()
                      .amount(
                          new CurrencyValue()
                              .amount(tx.getAmount())  // todo handle issued currencies
                              .currency(CurrencyEnum.DROPS)
                      )
                      .sourceAccount(tx.getAccount())
                      .destinationAccount(tx.getDestination())
                      .destinationTag(String.valueOf(tx.getDestinationTag()))
                      .fee(
                          new CurrencyValue()
                              .amount(tx.getFee())
                              .currency(CurrencyEnum.DROPS)
                      )
                      .type(tx.getTransactionType())
                      .hash(tx.getHash())
                      .status(toSimpleStatus(t.getMeta().getTransactionResult()));
                })
                .collect(Collectors.toList())
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
        .chainId(BlockchainAccountPayments.ChainIdEnum.XRP)
        .cursor(blockchainAccountTransactions.getCursor())
        .payments(
            transactions
              .stream()
              .filter(t -> "payment".equalsIgnoreCase(t.getType()))
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
    final List<BlockchainNetworkConfiguration> selectedNetworks = configuration.getNetworks()
        .stream()
        .filter(c -> c.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());

    if (selectedNetworks.size() == 0) {
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    final PublicRippledClient client = networkClientService.getClientForServer(selectedNetworks.get(0).getAddress());
    final TransactionResponse transaction = client.getTransaction(hash);
    final TransactionResult tx = transaction.getResult();
    return new BlockchainAccountTransaction()
        .network(network)
        .server(selectedNetworks.get(0).getAddress())
        .chainId(BlockchainAccountTransaction.ChainIdEnum.XRP)
        .transaction(
            new BlockchainAccountTransactionItem()
                .amount(
                    new CurrencyValue()
                        .amount(tx.getAmount())  // todo handle issued currencies
                        .currency(CurrencyEnum.DROPS)
                )
                .sourceAccount(tx.getAccount())
                .destinationAccount(tx.getDestination())
                .destinationTag(String.valueOf(tx.getDestinationTag()))
                .fee(
                    new CurrencyValue()
                        .amount(tx.getFee())
                        .currency(CurrencyEnum.DROPS)
                )
                .type(tx.getTransactionType())
                .hash(tx.getHash())
                .status(toSimpleStatus(tx.getMeta().getTransactionResult()))
        );
  }

  @Override
  public SubmitTransactionResult submitTransaction(String network,
      SubmitTransactionRequest submitTransactionRequest) throws UnknownNetworkException {
    final List<BlockchainNetworkConfiguration> selectedNetworks = configuration.getNetworks()
        .stream()
        .filter(c -> c.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());

    if (selectedNetworks.size() == 0) {
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    final PublicRippledClient client = networkClientService.getClientForServer(selectedNetworks.get(0).getAddress());
    final SubmitTransactionResponse submitTransaction = client
        .submitTransaction(submitTransactionRequest.getTransaction());
    final AccountTransaction tx = submitTransaction.getResult().getTxJson();
    return new SubmitTransactionResult()
        .chainId(SubmitTransactionResult.ChainIdEnum.XRP)
        .network(network)
        .server(selectedNetworks.get(0).getAddress())
        .transaction(
            new BlockchainAccountTransactionItem()
                .amount(
                    new CurrencyValue()
                        .amount(tx.getAmount())  // todo handle issued currencies
                        .currency(CurrencyEnum.DROPS)
                )
                .sourceAccount(tx.getAccount())
                .destinationAccount(tx.getDestination())
                .destinationTag(String.valueOf(tx.getDestinationTag()))
                .fee(
                    new CurrencyValue()
                        .amount(tx.getFee())
                        .currency(CurrencyEnum.DROPS)
                )
                .type(tx.getTransactionType())
                .hash(tx.getHash())
                .status(toSimpleStatus(tx.getMeta().getTransactionResult()))
        );
  }

  private String toSimpleStatus(String transactionResult) {
    // as per https://xrpl.org/transaction-results.html
    if (transactionResult.startsWith("tes") && transactionResult.endsWith("SUCCESS")) {
      return "success";
    }
    if (transactionResult.startsWith("tef") || transactionResult.startsWith("tec")
        || transactionResult.startsWith("tem")) {
      return "failure";
    }
    if (transactionResult.startsWith("tel") || transactionResult.startsWith("ter")) {
      return "pending";
    }
    // we don't understand this, return as is
    return transactionResult;
  }

  private String toURI(BlockchainNetworkConfiguration c) {
    try {
      return new URI(c.getAddress()).getHost();
    } catch (URISyntaxException e) {
      return null;
    }
  }


}
