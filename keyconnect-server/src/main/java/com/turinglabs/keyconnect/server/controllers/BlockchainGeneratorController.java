package com.turinglabs.keyconnect.server.controllers;

import com.turinglabs.keyconnect.api.client.model.CurrencyValue;
import com.turinglabs.keyconnect.api.client.model.CurrencyValue.CurrencyEnum;
import com.turinglabs.keyconnect.rippled.api.client.model.AccountTransaction;
import com.turinglabs.keyconnect.rippled.api.client.model.FeeResponse;
import com.turinglabs.keyconnect.server.factories.BlockchainGatewayFactory;
import com.turinglabs.keyconnect.server.gateways.BlockchainGateway;
import com.turinglabs.keyconnect.server.gateways.XrpGateway;
import com.turinglabs.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockchainGeneratorController {

  private final BlockchainGatewayFactory blockchainGatewayFactory;

  @Autowired
  public BlockchainGeneratorController(
      BlockchainGatewayFactory blockchainGatewayFactory) {
    this.blockchainGatewayFactory = blockchainGatewayFactory;
  }

  @GetMapping(
      path = "/v1/blockchains/xrp/generator/payment",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<AccountTransaction> generatePayment(
      @RequestParam("sourceAccount") String sourceAccount,
      @RequestParam("destinationAccount") String destinationAccount,
      @RequestParam(value = "destinationTag", required = false) int destinationTag,
      @RequestParam("amount") String amountInDrops,
      @RequestParam(value = "fee", required = false) String feeInDrops,
      @RequestParam(value = "network", required = false) String network
  ) throws UnknownNetworkException {
    final BlockchainGateway xrpGateway = blockchainGatewayFactory.getGateway(XrpGateway.CHAIN_ID);

    if (StringUtils.isBlank(feeInDrops)) {
      final CurrencyValue fee = xrpGateway.getFee(network).getFee();
      if (fee.getCurrency() == CurrencyEnum.DROPS) {
        feeInDrops = fee.getAmount();
      } else if (fee.getCurrency() == CurrencyEnum.XRP) {
        feeInDrops = new BigDecimal(fee.getAmount()).multiply(XrpGateway.DROPS_PER_XRP).toString();
      } else {
        // should never happen for XRP
        throw new IllegalArgumentException("Fee is in unsupported currency for XRP payment, fee=" + fee);
      }
    }

    return ResponseEntity.ok(new AccountTransaction()
        .transactionType("payment")
        .account(sourceAccount)
        .destination(destinationAccount)
        .destinationTag(BigDecimal.valueOf(destinationTag))
        .amount(amountInDrops)
        .fee(feeInDrops));
  }

}
