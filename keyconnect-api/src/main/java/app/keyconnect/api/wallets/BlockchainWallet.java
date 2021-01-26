package app.keyconnect.api.wallets;

import java.io.File;

public interface BlockchainWallet {

  void fundWallet();

  String getSeed();

  String getAddress();
}
