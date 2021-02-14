package app.keyconnect.sdk.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.util.List;

public interface BlockchainWalletFactory {

  /**
   * Generates a blockchain wallet given a blockchain ID
   *
   * @return New blockchain wallet
   */
  BlockchainWallet generateNext(String name);

  List<BlockchainWallet> getGeneratedWallets();

  void deleteLast();

  boolean hasWallets();

  String getChainIndex();

  ChainIdEnum getChainId();
}
