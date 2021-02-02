package app.keyconnect.api.wallets;

public interface BlockchainWallet {

  /**
   * Gets the wallet generation seed.
   * @return Wallet generation seed.
   */
  String getSeed();

  /**
   * Gets the wallet public address.
   * @return Wallet public address.
   */
  String getAddress();
}
