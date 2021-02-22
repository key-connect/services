package app.keyconnect.sdk.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedInteger;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
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
      final Transaction signedTransaction = Payment.builder().from(unsignedPayment).transactionSignature(signature).build();
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

  @Override
  public ChainIdEnum getChainId() {
    return ChainIdEnum.XRP;
  }
}
