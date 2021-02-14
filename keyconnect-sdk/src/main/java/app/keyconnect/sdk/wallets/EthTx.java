package app.keyconnect.sdk.wallets;

import java.math.BigInteger;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EthTx {

  private BigInteger nonce;
  private BigInteger gasPrice;
  private BigInteger gasLimit;
  private String to;
  private BigInteger value;
  private String data;

}
