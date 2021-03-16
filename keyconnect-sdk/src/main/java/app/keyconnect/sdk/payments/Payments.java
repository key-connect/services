package app.keyconnect.sdk.payments;

import app.keyconnect.api.client.BlockchainsApi;
import app.keyconnect.sdk.wallets.AddressableWallet;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nullable;

public class Payments {

  /**
   * Creates a {@link Payment} object
   * @param destinationWallet Destination wallet to send the money to
   * @param amount Amount of money to send
   * @return {@link Payment} object
   */
  public static Payment create(AddressableWallet destinationWallet, BigDecimal amount) {
    return Payment.builder()
        .to(destinationWallet.getAddress())
        .value(amount)
        .build();
  }

  /**
   * Creates a {@link Payment} object
   * @param destinationAddress Destination wallet address to send the money to
   * @param amount Amount of money to send
   * @return {@link Payment} object
   */
  public static Payment create(String destinationAddress, BigDecimal amount) {
    return Payment.builder()
        .to(destinationAddress)
        .value(amount)
        .build();
  }

  /**
   * Creates a {@link Payment} object
   * @param destinationAddress Destination wallet address to send the money to
   * @param amount Amount of money to send
   * @param fee Fee to set
   * @return {@link Payment} object
   */
  public static Payment create(String destinationAddress, BigDecimal amount, BigInteger fee) {
    return Payment.builder()
        .to(destinationAddress)
        .value(amount)
        .fee(fee)
        .build();
  }

  /**
   * Sends a payment based on the provided parameters, returning a {@link SubmittedPayment} object
   * @param sourceWallet Source {@link BlockchainWallet} to use for signing and sending payment
   * @param destinationWallet Destination {@link AddressableWallet} wallet to send money to
   * @param amount Amount of money to send
   * @param network Blockchain network to use (mainnet/testnet). Can be null for default network.
   * @return {@link SubmittedPayment} object
   * @throws SubmitPaymentException when there is an issue in submitting the payment to the chain
   */
  public static SubmittedPayment send(
      BlockchainWallet sourceWallet,
      AddressableWallet destinationWallet,
      BigDecimal amount,
      String network
  ) throws SubmitPaymentException {
    return send(
        sourceWallet, destinationWallet.getAddress(),
        amount,
        network
    );
  }

  /**
   * Sends a payment based on the provided parameters, returning a {@link SubmittedPayment} object.
   * This will send the payment to the default blockchain network (usually mainnet).
   * @param sourceWallet Source {@link BlockchainWallet} to use for signing and sending payment
   * @param destinationAddress Destination {@link AddressableWallet} wallet to send money to
   * @param amount Amount of money to send
   * @return {@link SubmittedPayment} object
   * @throws SubmitPaymentException when there is an issue in submitting the payment to the chain
   */
  public static SubmittedPayment send(
      BlockchainWallet sourceWallet,
      String destinationAddress,
      BigDecimal amount
  ) throws SubmitPaymentException {
    return send(sourceWallet, destinationAddress, amount, null);
  }

  /**
   * Sends a payment based on the provided parameters, returning a {@link SubmittedPayment} object.
   * @param sourceWallet Source {@link BlockchainWallet} to use for signing and sending payment
   * @param destinationAddress Destination wallet address to send money to
   * @param amount Amount of money to send
   * @param network Blockchain network to use (mainnet/testnet). Can be null for default network.
   * @return {@link SubmittedPayment} object
   * @throws SubmitPaymentException when there is an issue in submitting the payment to the chain
   */
  public static SubmittedPayment send(
      BlockchainWallet sourceWallet,
      String destinationAddress,
      BigDecimal amount,
      @Nullable String network
  ) throws SubmitPaymentException {
    return send(sourceWallet, destinationAddress, amount, null, network);
  }

  /**
   * Sends a payment based on the provided parameters, returning a {@link SubmittedPayment} object.
   * This will send the payment using the provided {@link BlockchainsApi} object, reusing connections.
   * @param sourceWallet Source {@link BlockchainWallet} to use for signing and sending payment
   * @param destinationAddress Destination wallet address to send money to
   * @param amount Amount of money to send
   * @param blockchainsApi Blockchains API object to use to send funds.
   * @param network Blockchain network to use (mainnet/testnet). Can be null for default network.
   * @return {@link SubmittedPayment} object
   * @throws SubmitPaymentException when there is an issue in submitting the payment to the chain
   */
  public static SubmittedPayment send(
      BlockchainWallet sourceWallet,
      String destinationAddress,
      BigDecimal amount,
      @Nullable BlockchainsApi blockchainsApi,
      String network
  ) throws SubmitPaymentException {
    final SignedPayment signedPayment = create(destinationAddress, amount)
        .sign(sourceWallet);

    if (null == blockchainsApi)
        return signedPayment.submit(network);
    else
      return signedPayment.submit(blockchainsApi, network);
  }
}
