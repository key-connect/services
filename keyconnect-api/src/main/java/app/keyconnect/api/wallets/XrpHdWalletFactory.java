package app.keyconnect.api.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.math.BigInteger;

public class XrpHdWalletFactory extends AbstractHdWalletFactory {

  public XrpHdWalletFactory(DeterministicWallet deterministicWallet) {
    super(deterministicWallet);
  }

  @Override
  BlockchainWallet buildWalletFromPrivateKey(String name, BigInteger privateKey) {
    return new XrpWallet(name, privateKey);
  }

  @Override
  public String getChainIndex() {
    return "144";
  }

  @Override
  public ChainIdEnum getChainId() {
    return ChainIdEnum.XRP;
  }
}
