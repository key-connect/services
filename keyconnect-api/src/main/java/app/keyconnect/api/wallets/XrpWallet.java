package app.keyconnect.api.wallets;

import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;
import org.xrpl.xrpl4j.codec.addresses.AddressCodec;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.addresses.VersionType;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

public class XrpWallet implements BlockchainWallet {

  private final WalletFactory walletFactory = DefaultWalletFactory.getInstance();
  private String name;
  private Wallet wallet;
  private String seed;

  public XrpWallet(String name, BigInteger privateKey) {
    this.name = name;
    // is there a better way than cutting the entropy down?
    final byte[] privateKeyBytes = ArrayUtils.subarray(privateKey.toByteArray(), 0, 16);
    this.seed = AddressCodec.getInstance()
        .encodeSeed(UnsignedByteArray.of(privateKeyBytes), VersionType.SECP256K1);
    this.wallet = walletFactory.fromSeed(seed, false);
  }

  @Override
  public String getAddress() {
    return wallet.classicAddress().value();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}
