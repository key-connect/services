package app.keyconnect.server.controllers;

import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainAccountPayments;
import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.server.factories.BlockchainGatewayFactory;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.server.services.rate.RateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockchainAccountController {

  private static final String DEFAULT_NETWORK_PARAM = "mainnet";
  private final BlockchainGatewayFactory blockchainGatewayFactory;
  private final RateHelper rateHelper;

  @Autowired
  public BlockchainAccountController(
      BlockchainGatewayFactory blockchainGatewayFactory,
      RateHelper rateHelper) {
    this.blockchainGatewayFactory = blockchainGatewayFactory;
    this.rateHelper = rateHelper;
  }

  @GetMapping(
      path = "/v1/blockchains/{chainId}/accounts/{accountId}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BlockchainAccountInfo> getBlockchainAccountInfo(
      @PathVariable("chainId") String chainId,
      @PathVariable("accountId") String accountId,
      @RequestParam(
          value = "network",
          required = false,
          defaultValue = DEFAULT_NETWORK_PARAM
      ) String network,
      @RequestParam(
          value = "fiat",
          required = false
      ) String fiat
  ) throws UnknownNetworkException {
    final BlockchainAccountInfo account = blockchainGatewayFactory.getGateway(chainId)
        .getAccount(network, accountId);
    rateHelper.applyFiatToAccount(fiat, account);
    return ResponseEntity.ok(
        account
    );
  }

  @PostMapping(
      path = "/v1/blockchains/{chainId}/accounts/{accountId}/fund",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Void> fundAccount(
      @PathVariable("chainId") String chainId,
      @PathVariable("accountId") String accountId,
      @RequestParam(
          value = "network",
          required = false,
          defaultValue = DEFAULT_NETWORK_PARAM
      ) String network,
      @RequestParam(
          value = "fiat",
          required = false
      ) String fiat
  ) throws UnknownNetworkException {
    blockchainGatewayFactory.getGateway(chainId)
        .fundAccount(network, accountId);
    return ResponseEntity.accepted().build();
  }

  @GetMapping(
      path = "/v1/blockchains/{chainId}/accounts/{accountId}/transactions",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BlockchainAccountTransactions> getTransactions(
      @PathVariable("chainId") String chainId,
      @PathVariable("accountId") String accountId,
      @RequestParam(value = "network", required = false, defaultValue = "mainnet") String network,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(value = "cursor", required = false) String cursor,
      @RequestParam(
          value = "fiat",
          required = false
      ) String fiat
  ) throws UnknownNetworkException {
    final BlockchainAccountTransactions transactions = blockchainGatewayFactory.getGateway(chainId)
        .getTransactions(accountId, network, limit, cursor);
    rateHelper.applyFiatValueToTransactions(fiat, transactions);
    return ResponseEntity.ok(
        transactions
    );
  }

  @GetMapping(
      path = "/v1/blockchains/{chainId}/accounts/{accountId}/payments",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BlockchainAccountPayments> getPayments(
      @PathVariable("chainId") String chainId,
      @PathVariable("accountId") String accountId,
      @RequestParam(value = "network", required = false, defaultValue = "mainnet") String network,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(value = "cursor", required = false) String cursor,
      @RequestParam(
          value = "fiat",
          required = false
      ) String fiat
  ) throws UnknownNetworkException {
    final BlockchainAccountPayments payments = blockchainGatewayFactory.getGateway(chainId)
        .getPayments(accountId, network, limit, cursor);
    rateHelper.applyFiatValueToPayments(fiat, payments);
    return ResponseEntity.ok(
        payments
    );
  }
}
