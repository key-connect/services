package app.keyconnect.api.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;

public interface BlockchainWalletFactory {

  BlockchainWallet generate(ChainIdEnum chainId);

}
