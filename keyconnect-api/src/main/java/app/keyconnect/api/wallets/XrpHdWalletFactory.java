package app.keyconnect.api.wallets;

import java.math.BigInteger;

public class XrpHdWalletFactory extends AbstractHdWalletFactory {

  public XrpHdWalletFactory(DeterministicWallet deterministicWallet) {
    super(deterministicWallet);
  }

  @Override
  BlockchainWallet buildWalletFromPrivateKey(BigInteger privateKey) {
    return new XrpWallet(privateKey);
  }

  @Override
  String getChainIndex() {
    return "144";
  }
}
