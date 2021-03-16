package app.keyconnect.sdk.payments;

import app.keyconnect.sdk.wallets.BlockchainWallet;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

/**
 * Basic Payment object depicting a specification for sending funds to a blockchain wallet.
 *
 * The source wallet information is intentionally left out since the source is identified by the
 * wallet that signs this object into a {@link SignedPayment} object.
 */
@Builder
@Getter
public class Payment {

  private final String to;
  private final BigDecimal value;
  @Nullable
  private final BigInteger fee;
  private final long nonce;

  /**
   * Converts a {@link Payment} object into a {@link SignedPayment} object
   * @param wallet Wallet to sign the {@link Payment} object with.
   * @return {@link SignedPayment} object
   */
  public SignedPayment sign(BlockchainWallet wallet) {
    return new SignedPayment(wallet, this);
  }
}
