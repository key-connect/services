package com.turinglabs.keyconnect.server.factories;

import com.turinglabs.keyconnect.server.gateways.BlockchainGateway;

public interface BlockchainGatewayFactory {

  BlockchainGateway getGateway(String blockchain);

  String[] getBlockchains();
}
