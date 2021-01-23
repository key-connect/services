package app.keyconnect.server.controllers;

import app.keyconnect.api.client.model.AccountsInfoRequest;
import app.keyconnect.api.client.model.AccountsInfoResponse;
import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.server.controllers.exceptions.BadRequestException;
import app.keyconnect.server.factories.BlockchainGatewayFactory;
import app.keyconnect.server.gateways.BlockchainGateway;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchBlockchainController {

  private static final int MAX_ACCOUNTS_INFO_BATCH_SIZE = 32;
  private final BlockchainGatewayFactory blockchainGatewayFactory;
  private final ExecutorService workPool = Executors.newWorkStealingPool(
      MAX_ACCOUNTS_INFO_BATCH_SIZE);

  @Autowired
  public BatchBlockchainController(
      BlockchainGatewayFactory blockchainGatewayFactory) {
    this.blockchainGatewayFactory = blockchainGatewayFactory;
  }

  @PostMapping(
      path = "/v1/batch/blockchains/accounts/info",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<AccountsInfoResponse> getBlockchainAccountInfo(
      @RequestBody AccountsInfoRequest accountsInfoRequest,
      @RequestParam(value = "network", required = false, defaultValue = "mainnet") String network
  ) {
    if (accountsInfoRequest.getAccounts() == null) {
      throw new BadRequestException("accounts field cannot be null");
    }

    if (accountsInfoRequest.getAccounts().size() > MAX_ACCOUNTS_INFO_BATCH_SIZE) {
      throw new BadRequestException("Number of accounts exceed the maximum allowed " + MAX_ACCOUNTS_INFO_BATCH_SIZE
          + " per batch request");
    }

    final List<BlockchainAccountInfo> blockchainAccountInfoList = accountsInfoRequest.getAccounts()
        .stream()
        .map(request -> workPool.submit(() -> {
          final BlockchainGateway gateway = blockchainGatewayFactory
              .getGateway(request.getChainId().getValue());
          return gateway.getAccount(network, request.getAccountId());
        }))
        .map(c -> {
          try {
            return c.get();
          } catch (InterruptedException | ExecutionException e) {
            // ignore
            return new BlockchainAccountInfo(); // fill in error
          }
        })
        .collect(Collectors.toList());
    return ResponseEntity.ok(
        new AccountsInfoResponse()
            .accounts(blockchainAccountInfoList)
    );
  }
}
