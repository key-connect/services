package app.keyconnect.api.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
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
  public String getChainIndex() {
    return "60";
  }

  @Override
  public ChainIdEnum getChainId() {
    return ChainIdEnum.ETH;
  }
}
