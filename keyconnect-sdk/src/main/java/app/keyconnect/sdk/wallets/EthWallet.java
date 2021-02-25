package app.keyconnect.sdk.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

public class EthWallet implements BlockchainWallet {

  public static final BigDecimal ETH_SCALE = BigDecimal.valueOf(1000000000000000000L);
  public static final String CHAIN_INDEX = "60";
  private final DeterministicSeed seed;
  private final DeterministicKeyChain chain;
  private final String passphrase;
  private Credentials credentials;
  private String name;

  /**
   * Creates an ETH wallet given a private key
   * @param name Name of the wallet
   * @param privateKey Private key
   */
  public EthWallet(String name, BigInteger privateKey) {
    this.name = name;
    this.seed = null;
    this.chain = null;
    this.passphrase = null;
    initCredentialsFromPrivateKey(privateKey);
  }

  /**
   * Creates a standalone ETH wallet
   * @param name Name of the wallet
   * @param passphrase Passphrase (salt) to use when generating wallet
   */
  public EthWallet(String name, String passphrase) {
    this.name = name;
    this.seed = new DeterministicSeed(
        new SecureRandom(),
        256,
        Optional.ofNullable(passphrase).orElse("")
    );
    this.passphrase = passphrase;
    this.chain = DeterministicKeyChain.builder().seed(seed).build();
    final DeterministicKey key = chain
        .getKeyByPath(HDUtils.parsePath("M/44H/" + CHAIN_INDEX + "H/0H/0/0"), true);
    final BigInteger privateKey = key.getPrivKey();
    initCredentialsFromPrivateKey(privateKey);
  }

  private void initCredentialsFromPrivateKey(BigInteger privateKey) {
    this.credentials = Credentials.create(privateKey.toString(16));
  }

  private String sign(RawTransaction tx) {
    byte[] signedMessage;
    signedMessage = TransactionEncoder.signMessage(tx, credentials);
    return Numeric.toHexString(signedMessage);
  }

  @Override
  public String buildPaymentTransaction(String to, BigDecimal valueInEth,
      @Nullable BigInteger gasFee, long sequence) {
    final RawTransaction rawTransaction =
        RawTransaction
            .createTransaction(BigInteger.valueOf(sequence), gasFee, BigInteger.valueOf(100000L),
                to, valueInEth.multiply(ETH_SCALE).toBigInteger(), "");

    return sign(rawTransaction);
  }

  @Override
  public String getAddress() {
    return this.credentials.getAddress();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Nullable
  public String getPassphrase() {
    return passphrase;
  }

  @Nullable
  public String getMnemonic() {
    if (null != seed && null != seed.getMnemonicCode()) {
      return String.join(" ", seed.getMnemonicCode());
    }

    return null;
  }

  @Override
  public ChainIdEnum getChainId() {
    return ChainIdEnum.ETH;
  }
}
