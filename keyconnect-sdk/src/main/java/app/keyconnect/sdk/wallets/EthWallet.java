package app.keyconnect.sdk.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.jetbrains.annotations.Nullable;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

public class EthWallet implements BlockchainWallet {

  public static final BigDecimal ETH_SCALE = BigDecimal.valueOf(1000000000000000000L);
  private final Credentials credentials;
  private String name;

  public EthWallet(String name, BigInteger privateKey) {
    this.name = name;
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

  @Override
  public ChainIdEnum getChainId() {
    return ChainIdEnum.ETH;
  }
}
