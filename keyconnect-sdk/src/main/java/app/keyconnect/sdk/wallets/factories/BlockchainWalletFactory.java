package app.keyconnect.sdk.wallets.factories;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.sdk.wallets.BlockchainWallet;
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
