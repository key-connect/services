package app.keyconnect.api.wallets;

import java.util.List;

public interface BlockchainWalletFactory {

  /**
   * Generates a blockchain wallet given a blockchain ID
   *
   * @return New blockchain wallet
   */
  BlockchainWallet generateNext();

  List<BlockchainWallet> getGeneratedWallets();

  void deleteLast();
}
