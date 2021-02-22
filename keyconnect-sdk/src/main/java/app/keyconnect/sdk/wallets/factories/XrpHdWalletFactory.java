package app.keyconnect.sdk.wallets.factories;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import app.keyconnect.sdk.wallets.DeterministicWallet;
import app.keyconnect.sdk.wallets.XrpWallet;
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
