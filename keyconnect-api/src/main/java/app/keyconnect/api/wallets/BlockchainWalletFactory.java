package app.keyconnect.api.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;

public interface BlockchainWalletFactory {

  /**
   * Generates a blockchain wallet given a blockchain ID
   * @param chainId Blockchain ID, one of ETH | XRP.
   * @return New blockchain wallet
   */
  BlockchainWallet generate(ChainIdEnum chainId);

}
