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
import app.keyconnect.rippled.api.client.model.AccountTransactionMeta;
import app.keyconnect.rippled.api.client.model.AccountTransactionResponse;
import app.keyconnect.rippled.api.client.model.FeeResponse;
import app.keyconnect.rippled.api.client.model.FeeResult;
import app.keyconnect.rippled.api.client.model.ServerInfoResponse;
import app.keyconnect.rippled.api.client.model.SubmitTransactionResponse;
import app.keyconnect.rippled.api.client.model.TransactionResponse;
import app.keyconnect.rippled.api.client.model.TransactionResult;
import app.keyconnect.server.controllers.exceptions.InvalidCursorException;
import app.keyconnect.server.factories.configuration.BlockchainNetworkConfiguration;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.server.services.networks.NetworkClient;
import app.keyconnect.server.services.networks.NetworkClientService;
import com.google.common.base.Strings;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest;
import org.xrpl.xrpl4j.model.transactions.Address;

public class XrpGateway implements BlockchainGateway {

  private static final Logger logger = LoggerFactory.getLogger(XrpGateway.class);
  private final FaucetClient faucetClient =
      FaucetClient.construct(HttpUrl.parse("https://faucet.altnet.rippletest.net"));
  public static final String CHAIN_ID = "xrp";
  public static final BigDecimal DROPS_PER_XRP = BigDecimal.valueOf(1000000);
  private static final String DEFAULT_NETWORK = "mainnet";
  private static final int XRP_SCALE = 18;
  private static final String STATUS_ERROR = "error";
  private final NetworkClientService<PublicRippledClient> networkClientService;

  public XrpGateway(NetworkClientService<PublicRippledClient> networkClientService) {
    this.networkClientService = networkClientService;
  }

  @Override
  public String getChainId() {
    return CHAIN_ID;
  }

  @Override
  public String[] getNetworks() {
    return networkClientService.getNetworks()
        .stream()
        .map(BlockchainNetworkConfiguration::getGroup)
        .distinct()
        .toArray(String[]::new);
  }

  @Override
  public String validateNetworkOrDefault(String network) throws UnknownNetworkException {
    if (Strings.isNullOrEmpty(network)) {
      return DEFAULT_NETWORK;
    }

    if (networkClientService.getNetworks()
        .stream()
        .anyMatch(n -> n.getGroup().equalsIgnoreCase(network))
    ) {
      return network;
    }

    throw new UnknownNetworkException(CHAIN_ID, network);
  }

  @Override
  @Cacheable("fast")
  public BlockchainNetworkServerStatus[] getNetworkServerStatus(String network)
      throws UnknownNetworkException {
    final Set<NetworkClient<PublicRippledClient>> clients = networkClientService
        .getAllMatching(network);

    if (clients.size() == 0) {
      // we could not find the specified network
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    return clients.stream()
        .map(networkClient -> {
          final BlockchainNetworkConfiguration networkConfiguration = networkClient.getNetwork();
          final String serverUrl = networkConfiguration.getAddress();
          try {
            final ServerInfoResponse serverInfoResponse = networkClientService
                .getClientForServer(serverUrl)
                .getServerInfo();
            // todo refine the way the BlockchainNetworkStatus is constructed
            return new BlockchainNetworkServerStatus()
                // todo be defensive, this is relying too much on rippled behaving correctly
                .status(serverInfoResponse.getResult().getStatus().equalsIgnoreCase("success")
                    ? StatusEnum.HEALTHY : StatusEnum.UNHEALTHY)
                .host(toURI(serverUrl))
                // todo either remove lastCheck or find more reliable way to get this
                .lastCheck(Instant.now().toString());
          } catch (Throwable e) {
            logger.warn("Unable to get serverInfo to obtain status for network=" + network, e);
            return new BlockchainNetworkServerStatus()
                .status(StatusEnum.UNHEALTHY)
                .host(toURI(serverUrl))
                .lastCheck(Instant.now().toString());
          }
        })
        .toArray(BlockchainNetworkServerStatus[]::new);
  }

  @Cacheable(value = "fast")
  public BlockchainFee getFee(String specifiedNetwork) throws UnknownNetworkException {
    final String network = validateNetworkOrDefault(specifiedNetwork);
    final Set<NetworkClient<PublicRippledClient>> clients = networkClientService
        .getAllMatching(network);
    if (clients.size() == 0) {
      // we could not find the specified network
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    for (NetworkClient<PublicRippledClient> client : clients) {
      final BlockchainNetworkConfiguration networkConfig = client.getNetwork();
      final FeeResponse feeResponse = networkClientService
          .getClientForServer(networkConfig.getAddress())
          .getFee();
      final FeeResult feeResult = feeResponse.getResult();

      if (feeResult == null
        || (StringUtils.isNotBlank(feeResult.getStatus()) && feeResult.getStatus().equalsIgnoreCase(STATUS_ERROR))) {
        continue;
      }

      final CurrencyValue fee = new CurrencyValue()
          .amount(feeResult.getDrops().getMinimumFee())
          .currency(CurrencyEnum.DROPS);
      return new BlockchainFee()
          .chainId(BlockchainFee.ChainIdEnum.XRP)
          .fee(fee)
          .network(network)
          .server(toURI(networkConfig.getAddress()));
    }

    throw new ResponseStatusException(HttpStatus.NO_CONTENT, CHAIN_ID + " " + network + " did not return any valid fee at this time. Please try again.");
  }

  @Override
  @Cacheable(value = "slow")
  public BlockchainAccountInfo getAccount(String network, String accountId)
      throws UnknownNetworkException {
    final Set<NetworkClient<PublicRippledClient>> networkClients = networkClientService
        .getAllMatching(network);
    if (networkClients.size() == 0) {
      // we could not find the specified network
      throw new UnknownNetworkException(CHAIN_ID, network);
    }
    // for mainnet xrp there would be two of these
    // we do this to allow fallback
    AccountInfoResponse accountInfoResponse = null;
    BlockchainNetworkConfiguration selectedNetwork = null;
    for (NetworkClient<PublicRippledClient> networkClient : networkClients) {
      final BlockchainNetworkConfiguration networkConfig = networkClient.getNetwork();
      try {
        accountInfoResponse = networkClientService
            .getClientForServer(networkConfig.getAddress())
            .getAccountInfo(accountId);
        selectedNetwork = networkConfig;
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
        && StringUtils.isNotBlank(accountInfoResponse.getResult().getStatus())
        && !accountInfoResponse.getResult().getStatus().equalsIgnoreCase(STATUS_ERROR)
        && accountInfoResponse.getResult().getAccountData() != null) {
      if (accountInfoResponse.getResult().getAccountData().getBalance() != null) {
        final BigDecimal balanceInXrp = new BigDecimal(
            accountInfoResponse.getResult().getAccountData().getBalance())
            .divide(DROPS_PER_XRP, XRP_SCALE, RoundingMode.DOWN);

        accountInfo.setBalance(
            new CurrencyValue()
                .amount(balanceInXrp.toPlainString())
                .currency(CurrencyEnum.XRP)
        );
      }

      accountInfo.setLastTransactionId(
          accountInfoResponse.getResult().getAccountData().getPreviousTxnID());
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested account " + accountId + " was not found on " + CHAIN_ID + " " + network);
    }

    return accountInfo;
  }

  @Override
  public void fundAccount(String network, String accountId) {
    if (!network.equals("testnet")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Automatic funding is not available in " + network + " network.");

    faucetClient.fundAccount(FundAccountRequest.of(
        Address.of(accountId)
    ));
  }

  @Override
  @Cacheable(value = "slow")
  public BlockchainAccountTransactions getTransactions(String accountId, String network,
      int limit, String cursor)
      throws UnknownNetworkException {
    final Set<NetworkClient<PublicRippledClient>> networkClients = networkClientService
        .getAllMatching(network);

    if (networkClients.size() == 0) {
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    for (NetworkClient<PublicRippledClient> networkClient : networkClients) {
      final PublicRippledClient client = networkClient.getClient();
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

      if (transactions == null ||
          (StringUtils.isNotBlank(accountTransactionsResponse.getResult().getStatus()) && accountTransactionsResponse.getResult().getStatus().equalsIgnoreCase(STATUS_ERROR))
      ) {
        continue;
      }

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
          .server(toURI(networkClient.getNetwork().getAddress()))
          .chainId(BlockchainAccountTransactions.ChainIdEnum.XRP)
          .cursor(cursor)
          .transactions(
              transactions.stream()
                  .map(t -> {
                    final AccountTransaction tx = t.getTx();
                    return new BlockchainAccountTransactionItem()
                        .amount(
                            fromDropsString(tx.getAmount())
                        )
                        .sourceAccount(tx.getAccount())
                        .destinationAccount(tx.getDestination())
                        .destinationTag(String.valueOf(tx.getDestinationTag()))
                        .fee(
                            fromDropsString(tx.getFee())
                        )
                        .type(tx.getTransactionType())
                        .hash(tx.getHash())
                        .status(toSimpleStatus(t.getMeta().getTransactionResult()));
                  })
                  .collect(Collectors.toList())
          );
    }

    // if we got here then we didn't find any transctions
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested transactions could not be found for account " + accountId + " on chain " + CHAIN_ID + " " + network);
  }

  private CurrencyValue fromDropsString(String dropsAmount) {
    return new CurrencyValue()
        .amount(
            new BigDecimal(dropsAmount)
                .divide(DROPS_PER_XRP, XRP_SCALE, RoundingMode.DOWN)
                .toString()
        )  // todo handle issued currencies
        .currency(CurrencyEnum.XRP);
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
  @Caching(
      cacheable = {
          @Cacheable(value = "elephant", unless = "#result.transaction == null "
              + "|| (#result.transaction.status.equalsIgnoreCase('success') == false "
              + "&& #result.transaction.status.equalsIgnoreCase('failure') == false)"),
          @Cacheable(value = "slow", unless = "#result.transaction == null || #result.transaction.status.equalsIgnoreCase('ok') == true")
      }
  )
  public BlockchainAccountTransaction getTransaction(String network, String hash)
      throws UnknownNetworkException {
    final Set<NetworkClient<PublicRippledClient>> networkClients = networkClientService
        .getAllMatching(network);
    if (networkClients.size() == 0) {
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    for (NetworkClient<PublicRippledClient> networkClient : networkClients) {
      final PublicRippledClient client = networkClient.getClient();
      final TransactionResponse transaction = client.getTransaction(hash);
      final TransactionResult tx = transaction.getResult();
      if (tx == null ||
          (StringUtils.isNotBlank(tx.getStatus()) && tx.getStatus().equalsIgnoreCase(STATUS_ERROR))
      ) {
        continue;
      }

      final AccountTransactionMeta meta = tx.getMeta();

      String amountString = tx.getAmount();
      if (StringUtils.isBlank(amountString)) {
        amountString = "0";
      }
      final BigDecimal txAmount = new BigDecimal(amountString)
          .divide(DROPS_PER_XRP, RoundingMode.DOWN);

      final String txFee = StringUtils.isBlank(tx.getFee()) ? "0" : tx.getFee();
      return new BlockchainAccountTransaction()
          .network(network)
          .server(toURI(networkClient.getNetwork().getAddress()))
          .chainId(BlockchainAccountTransaction.ChainIdEnum.XRP)
          .transaction(
              new BlockchainAccountTransactionItem()
                  .amount(
                      new CurrencyValue()
                          .amount(
                              txAmount.toString()
                          )  // todo handle issued currencies
                          .currency(CurrencyEnum.XRP)
                  )
                  .sourceAccount(tx.getAccount())
                  .destinationAccount(tx.getDestination())
                  .destinationTag(String.valueOf(tx.getDestinationTag()))
                  .fee(
                      new CurrencyValue()
                          .amount(txFee)
                          .currency(CurrencyEnum.DROPS)
                  )
                  .type(tx.getTransactionType())
                  .hash(tx.getHash())
                  .status(meta != null ? toSimpleStatus(meta.getTransactionResult()) : "unknown")
          );
    }

    // if we're here then we didn't find the transaction
    throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Requested transaction " + hash + " was not found on " + CHAIN_ID + " " + network
    );
  }

  @Override
  public SubmitTransactionResult submitTransaction(String network,
      SubmitTransactionRequest submitTransactionRequest) throws UnknownNetworkException {
    final Set<NetworkClient<PublicRippledClient>> networkClients = networkClientService
        .getAllMatching(network);

    if (networkClients.size() == 0) {
      throw new UnknownNetworkException(CHAIN_ID, network);
    }

    for (NetworkClient<PublicRippledClient> networkClient : networkClients) {
      final PublicRippledClient client = networkClient.getClient();
      final SubmitTransactionResponse submitTransaction = client
          .submitTransaction(submitTransactionRequest.getTransaction());
      final AccountTransaction tx = submitTransaction.getResult().getTxJson();
      return new SubmitTransactionResult()
          .chainId(SubmitTransactionResult.ChainIdEnum.XRP)
          .network(network)
          .server(networkClient.getNetwork().getAddress())
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
                  .status(toSimpleStatus(submitTransaction.getResult().getEngineResult()))
          );
    }
    return null;
  }

  private String toSimpleStatus(String transactionResult) {
    // as per https://xrpl.org/transaction-results.html
    if (transactionResult == null) {
      return "null";
    }

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

  private String toURI(String address) {
    try {
      return new URI(address).getHost();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  private String toURI(BlockchainNetworkConfiguration c) {
    try {
      return new URI(c.getAddress()).getHost();
    } catch (URISyntaxException e) {
      return null;
    }
  }


}
