package app.keyconnect.server.controllers;

import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainAccountPayments;
import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.api.client.model.CurrencyValue;
import app.keyconnect.api.client.model.GenericCurrencyValue;
import app.keyconnect.server.factories.BlockchainGatewayFactory;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.server.services.rate.RateService;
import app.keyconnect.server.services.rate.models.Rate;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockchainAccountController {

  private static final String DEFAULT_NETWORK_PARAM = "mainnet";
  private final BlockchainGatewayFactory blockchainGatewayFactory;
  private final RateService rateService;

  @Autowired
  public BlockchainAccountController(
      BlockchainGatewayFactory blockchainGatewayFactory,
      RateService rateService) {
    this.blockchainGatewayFactory = blockchainGatewayFactory;
    this.rateService = rateService;
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
    if (!StringUtils.isBlank(fiat)
        && account != null
        && account.getBalance() != null
        && !StringUtils.isBlank(account.getBalance().getAmount())) {
      final CurrencyValue balance = account.getBalance();
      final Rate rate = rateService.getRate(balance.getCurrency().getValue(), fiat);
      if (rate != null) {
        account.value(
            new GenericCurrencyValue()
                .amount(
                    rate.calculate(
                        new BigDecimal(balance.getAmount()))
                        .toString()
                )
                .currency(fiat)
        );
      }
    }
    return ResponseEntity.ok(
        account
    );
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
      @RequestParam(value = "cursor", required = false) String cursor
  ) throws UnknownNetworkException {
    return ResponseEntity.ok(
        blockchainGatewayFactory.getGateway(chainId)
            .getTransactions(accountId, network, limit, cursor)
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
      @RequestParam(value = "cursor", required = false) String cursor
  ) throws UnknownNetworkException {
    return ResponseEntity.ok(
        blockchainGatewayFactory.getGateway(chainId)
            .getPayments(accountId, network, limit, cursor)
    );
  }

}
