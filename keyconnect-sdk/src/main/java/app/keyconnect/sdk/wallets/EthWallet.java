package app.keyconnect.sdk.wallets;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

public class EthWallet implements BlockchainWallet {

  private final Credentials credentials;
  private String name;

  public EthWallet(String name, BigInteger privateKey) {
    this.name = name;
    this.credentials = Credentials.create(privateKey.toString(16));
  }

  public String signTransaction(EthTx ethTx) {
    final byte[] signedTransaction = TransactionEncoder.signMessage(
        RawTransaction.createTransaction(ethTx.getNonce(), ethTx.getGasPrice(), ethTx.getGasLimit(), ethTx.getTo(), ethTx.getValue(), ethTx.getData()),
        credentials);
    return Numeric.toHexString(signedTransaction);
  }

  @Override
  public String buildPaymentTransaction(String to, BigDecimal valueInEth,
      @Nullable BigInteger gasFee, long sequence) {
    throw new NotImplementedException();
  }

  /**
   * Signs given encoded {@link RawTransaction} into encoded signed transaction
   * @param data Encoded {@link RawTransaction} bytes
   * @return Encoded signed transction data
   */
  /*public byte[] sign(byte[] data) {
    TransactionDecoder.decode(data)
    return TransactionEncoder.encode(
        Sign.signMessage(data, credentials.getEcKeyPair())
    );
  }*/

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
}
