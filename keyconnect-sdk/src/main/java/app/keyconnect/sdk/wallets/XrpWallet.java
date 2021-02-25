package app.keyconnect.sdk.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedInteger;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.web3j.crypto.Credentials;
import org.xrpl.xrpl4j.codec.addresses.AddressCodec;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.addresses.VersionType;
import org.xrpl.xrpl4j.codec.binary.XrplBinaryCodec;
import org.xrpl.xrpl4j.keypairs.DefaultKeyPairService;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.ImmutablePayment.Builder;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

public class XrpWallet implements BlockchainWallet {

  public static final String CHAIN_INDEX = "144";
  private final WalletFactory walletFactory = DefaultWalletFactory.getInstance();
  private final DeterministicKeyChain chain;
  private final String passphrase;
  private String name;
  private final DeterministicSeed seed;
  private Wallet wallet;

  /**
   * Creates XRP Wallet given a private key
   * @param name Name of the wallet
   * @param privateKey Private key
   */
  public XrpWallet(String name, BigInteger privateKey) {
    this.name = name;
    this.passphrase = null;
    this.chain = null;
    this.seed = null;

    initWalletFromPrivKey(privateKey);
  }

  /**
   * Creates a standalone XRP Wallet
   * @param name Name of the wallet
   * @param passphrase Passphrase (salt) to use when generating wallet
   */
  public XrpWallet(String name, String passphrase) {
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
    final BigInteger privKey = key.getPrivKey();
    initWalletFromPrivKey(privKey);
  }

  /**
   * Recovers a XRP wallet given a passphrase (optional) and mnemonic
   * @param name Name of the wallet
   * @param passphrase Wallet passphrase (salt) - optional
   * @param mnemonic Mnemonic to recover from
   */
  public XrpWallet(String name, String passphrase, String mnemonic) {
    this.name = name;
    this.passphrase = passphrase;
    try {
      this.seed = new DeterministicSeed(
          mnemonic,
          null,
          Optional.ofNullable(passphrase).orElse(""),
          Instant.now().getEpochSecond()
      );
    } catch (UnreadableWalletException e) {
      throw new RuntimeException("Unreadable wallet", e);
    }
    this.chain = DeterministicKeyChain.builder().seed(seed).build();
    final DeterministicKey key = chain
        .getKeyByPath(HDUtils.parsePath("M/44H/" + CHAIN_INDEX + "H/0H/0/0"), true);
    final BigInteger privKey = key.getPrivKey();
    initWalletFromPrivKey(privKey);
  }

  private void initWalletFromPrivKey(BigInteger privKey) {
    final byte[] privateKeyBytes = ArrayUtils.subarray(privKey.toByteArray(), 0, 16);
    final String seed = AddressCodec.getInstance()
        .encodeSeed(UnsignedByteArray.of(privateKeyBytes), VersionType.SECP256K1);
    this.wallet = walletFactory.fromSeed(seed, false);
  }

  @Override
  public String buildPaymentTransaction(String to, BigDecimal valueInXrp, @Nullable BigInteger fee,
      long sequence) {
    final Optional<BigInteger> feeValue = Optional.ofNullable(fee);
    final String destination;
    final Optional<String> tag;

    if (to.contains(":")) {
      final String[] destinationAndTag = to.split(":");
      destination = destinationAndTag[0];
      tag = Optional.of(destinationAndTag[1]);
    } else {
      destination = to;
      tag = Optional.empty();
    }

    final Builder paymentBuilder = Payment.builder()
        .account(wallet.classicAddress())
        .amount(XrpCurrencyAmount.ofXrp(valueInXrp))
        .destination(Address.builder().value(destination).build())
        .sequence(UnsignedInteger.valueOf(sequence))
        .signingPublicKey(wallet.publicKey());

    tag.ifPresent(t -> paymentBuilder.destinationTag(UnsignedInteger.valueOf(t)));

    // todo get fee from keyconnect API
    feeValue.ifPresent(f -> paymentBuilder.fee(XrpCurrencyAmount.ofDrops(fee.longValue())));

    final Payment unsignedPayment = paymentBuilder.build();

    final ObjectMapper objectMapper = ObjectMapperFactory.create();
    try {
      final String serialisedPayment = objectMapper.writeValueAsString(unsignedPayment);
      final XrplBinaryCodec binaryCodec = new XrplBinaryCodec();
      final String encodedHex = binaryCodec.encodeForSigning(serialisedPayment);
      final String signature = DefaultKeyPairService.getInstance()
          .sign(encodedHex, wallet.privateKey().get());
      final Transaction signedTransaction = Payment.builder().from(unsignedPayment)
          .transactionSignature(signature).build();
      final String signedJson = objectMapper.writeValueAsString(signedTransaction);
      return binaryCodec.encode(signedJson);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Transaction signing failed", e);
    }
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
    return ChainIdEnum.XRP;
  }
}
