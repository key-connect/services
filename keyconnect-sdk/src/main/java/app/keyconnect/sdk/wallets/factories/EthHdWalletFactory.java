package app.keyconnect.sdk.wallets.factories;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import app.keyconnect.sdk.wallets.DeterministicWallet;
import app.keyconnect.sdk.wallets.EthWallet;
import java.math.BigInteger;

public class EthHdWalletFactory extends AbstractHdWalletFactory {

  public EthHdWalletFactory(DeterministicWallet deterministicWallet) {
    super(deterministicWallet);
  }

  @Override
  BlockchainWallet buildWalletFromPrivateKey(String name, BigInteger privateKey) {
    return new EthWallet(name, privateKey);
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
