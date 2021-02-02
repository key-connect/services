package app.keyconnect.api.wallets;

public interface BlockchainWallet {

  /**
   * Funds the wallet. This method should only be operable in test environments.
   */
  void fundWallet();

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
