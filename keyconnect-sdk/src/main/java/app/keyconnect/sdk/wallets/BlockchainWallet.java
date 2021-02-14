package app.keyconnect.sdk.wallets;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nullable;

public interface BlockchainWallet {

  String buildPaymentTransaction(String to, BigDecimal valueInXrp, @Nullable BigInteger fee);

  /**
   * Gets the wallet public address.
   *
   * @return Wallet public address.
   */
  String getAddress();

  /**
   * User-friendly name of the wallet
   * @return Wallet name
   */
  String getName();

  void setName(String name);
}
