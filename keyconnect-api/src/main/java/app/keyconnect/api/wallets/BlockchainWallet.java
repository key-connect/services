package app.keyconnect.api.wallets;

public interface BlockchainWallet {

  /**
   * Gets the wallet public address.
   *
   * @return Wallet public address.
   */
  String getAddress();
}
