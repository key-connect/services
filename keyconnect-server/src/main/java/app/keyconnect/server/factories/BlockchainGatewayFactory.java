package app.keyconnect.server.factories;

import app.keyconnect.server.gateways.BlockchainGateway;

public interface BlockchainGatewayFactory {

  BlockchainGateway getGateway(String blockchain);

  String[] getBlockchains();
}
