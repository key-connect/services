package app.keyconnect.sdk.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;

public interface AddressableWallet {

  /**
   * Gets the wallet public address.
   *
   * @return Wallet public address.
   */
  String getAddress();

  /**
   * Gets user-friendly name of the wallet
   *
   * @return Wallet name
   */
  String getName();

  /**
   * Sets user-friendly name of the wallet
   *
   * @param name Wallet name
   */
  void setName(String name);

  /**
   * Gets the ID of the blockchain that this wallet belongs to
   * @return chainId
   */
  ChainIdEnum getChainId();

}
