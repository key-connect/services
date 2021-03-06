package app.keyconnect.sdk.wallets;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nullable;

public interface BlockchainWallet {

  /**
   * Build payment transaction given the parameters and return a signed transaction that is
   *   ready to be sent
   * @param to Recipient address
   * @param value Payment value
   * @param fee Fee value
   * @param sequence Blockchain sequence to use (if relevant)
   * @return A string representing a signed transaction that can be submitted to the blockchain
   */
  String buildPaymentTransaction(String to, BigDecimal value, @Nullable BigInteger fee,
      long sequence);

  /**
   * Gets the wallet public address.
   *
   * @return Wallet public address.
   */
  String getAddress();

  /**
   * Gets user-friendly name of the wallet
   *
   * @return Wallet name
   */
  String getName();

  /**
   * Sets user-friendly name of the wallet
   *
   * @param name Wallet name
   */
  void setName(String name);

  /**
   * Returns passphrase (salt) if the wallet is a standalone wallet
   * @return String Passphrase or null
   */
  String getPassphrase();

  /**
   * Returns mnemonic if the wallet is a standalone wallet
   * @return String Mnemonic or null
   */
  String getMnemonic();

  ChainIdEnum getChainId();
}
