package app.keyconnect.api.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;

public interface BlockchainWalletFactory {

  /**
   * Generates a blockchain wallet given a blockchain ID
   * @param chainId Blockchain ID, one of ETH | XRP.
   * @return New blockchain wallet
   */
  BlockchainWallet generate(ChainIdEnum chainId);

  /**
   * Generates a blockchain wallet from the provided seed, for the specified blockchain ID
   * @param chainId Blockchain ID, one of ETH | XRP.
   * @param seed Wallet seed
   * @return Restored blockchain wallet
   */
  BlockchainWallet fromSeed(ChainIdEnum chainId, String seed);

}
