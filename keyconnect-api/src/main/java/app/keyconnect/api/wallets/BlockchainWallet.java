package app.keyconnect.api.wallets;

public interface BlockchainWallet {

  /**
   * Gets the wallet public address.
   *
   * @return Wallet public address.
   */
  String getAddress();

  /**
   * User-friendly name of the wallet
   * @return Wallet name
   */
  String getName();

  void setName(String name);
}
