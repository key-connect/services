package app.keyconnect.sdk.wallets;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Payment {

  private String to;
  private BigDecimal value;
  @Nullable
  private BigInteger fee;
  private long nonce;

  public SignedPayment sign(BlockchainWallet wallet) {
    return new SignedPayment(wallet, this);
  }
}
