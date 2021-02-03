package app.keyconnect.api.wallets;

import java.math.BigInteger;

public class EthHdWalletFactory extends AbstractHdWalletFactory {

  public EthHdWalletFactory(DeterministicWallet deterministicWallet) {
    super(deterministicWallet);
  }

  @Override
  BlockchainWallet buildWalletFromPrivateKey(BigInteger privateKey) {
    return new EthWallet(privateKey);
  }

  @Override
  String getChainIndex() {
    return "60";
  }
}
