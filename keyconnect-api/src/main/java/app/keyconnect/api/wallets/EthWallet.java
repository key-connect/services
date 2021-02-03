package app.keyconnect.api.wallets;

import java.math.BigInteger;
import org.web3j.crypto.Credentials;

public class EthWallet implements BlockchainWallet {

  private final Credentials credentials;

  public EthWallet(BigInteger privateKey) {
    this.credentials = Credentials.create(privateKey.toString(16));
  }

  @Override
  public String getAddress() {
    return this.credentials.getAddress();
  }
}
