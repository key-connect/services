package app.keyconnect.server.controllers;

import app.keyconnect.server.controllers.exceptions.BadRequestException;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.api.client.model.AvailableBlockchains;
import app.keyconnect.api.client.model.BlockchainFee;
import app.keyconnect.api.client.model.BlockchainNetwork;
import app.keyconnect.api.client.model.BlockchainStatus;
import app.keyconnect.server.factories.BlockchainGatewayFactory;
import app.keyconnect.server.gateways.BlockchainGateway;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockchainStatusController {

  private final BlockchainGatewayFactory gatewayFactory;

  @Autowired
  public BlockchainStatusController(
      BlockchainGatewayFactory gatewayFactory) {
    this.gatewayFactory = gatewayFactory;
  }

  @GetMapping(
      path = "/v1/blockchains/status",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<AvailableBlockchains> getBlockchainsStatus() {
    return ResponseEntity.ok(
        new AvailableBlockchains()
            .blockchains(
                Arrays.stream(gatewayFactory.getBlockchains())
                    .map(gatewayFactory::getGateway)
                    .map(g ->
                        new BlockchainStatus()
                            .chainId(g.getChainId())
                            .networks(
                                Arrays.stream(g.getNetworks())
                                    .map(n -> {
                                      try {
                                        return new BlockchainNetwork()
                                            .group(n)
                                            .servers(Arrays.asList(g.getNetworkServerStatus(n)));
                                      } catch (UnknownNetworkException e) {
                                        throw new BadRequestException(e);
                                      }
                                    })
                                    .collect(Collectors.toList()))
                    ).collect(Collectors.toList())
            )
    );
  }

  @GetMapping(
      path = "/v1/blockchains/{chainId}/status",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BlockchainStatus> getBlockchainStatus(
      @PathVariable("chainId") String chainId,
      @RequestParam(value = "network", required = false, defaultValue = "mainnet") String network
  ) {
    final BlockchainGateway g = gatewayFactory.getGateway(chainId);
    if (network != null) {
      return ResponseEntity.ok(
          new BlockchainStatus()
              .chainId(chainId)
              .networks(
                  Stream.of(network)
                      .map(n -> {
                        try {
                          return new BlockchainNetwork()
                              .group(n)
                              .servers(Arrays.asList(
                                  g.getNetworkServerStatus(n)
                              ));
                        } catch (UnknownNetworkException e) {
                          throw new BadRequestException(e);
                        }
                      })
                      .collect(Collectors.toList())
              )
      );
    }

    return ResponseEntity.ok(
        new BlockchainStatus()
            .chainId(chainId)
            .networks(
                Stream.of(g.getNetworks())
                    .map(n -> {
                      try {
                        return new BlockchainNetwork()
                            .group(n)
                            .servers(Arrays.asList(
                                g.getNetworkServerStatus(n)
                            ));
                      } catch (UnknownNetworkException e) {
                        throw new BadRequestException(e);
                      }
                    })
                    .collect(Collectors.toList())
            )
    );
  }

  @GetMapping(
      path = "/v1/blockchains/{chainId}/fee",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BlockchainFee> getBlockchainFee(
      @PathVariable("chainId") String chainId,
      @RequestParam(value = "network", required = false, defaultValue = "mainnet") String network
  ) throws UnknownNetworkException {
    final BlockchainGateway gateway = gatewayFactory.getGateway(chainId);
    final BlockchainFee fee = gateway.getFee(network);
    return ResponseEntity.ok(fee);
  }
}
