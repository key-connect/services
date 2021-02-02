package app.keyconnect.api.wallets;

import java.math.BigInteger;
import org.web3j.crypto.Credentials;

public class EthWallet implements BlockchainWallet {

  private final DeterministicWalletFactory deterministicWalletFactory;
  private final BigInteger privKey;
  private final Credentials credentials;

  public EthWallet(DeterministicWalletFactory deterministicWalletFactory) {
    this.deterministicWalletFactory = deterministicWalletFactory;
    this.privKey = this.deterministicWalletFactory.getPrivKey();
    this.credentials = Credentials.create(privKey.toString(16));
  }

  @Override
  public String getSeed() {
    return this.deterministicWalletFactory.getMnemonicCode();
  }

  @Override
  public String getAddress() {
    return this.credentials.getAddress();
  }
}
