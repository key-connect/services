package app.keyconnect.sdk.payments;

import app.keyconnect.api.client.BlockchainsApi;
import app.keyconnect.sdk.wallets.AddressableWallet;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nullable;

public class Payments {

  public static Payment create(AddressableWallet destinationWallet, BigDecimal amount) {
    return Payment.builder()
        .to(destinationWallet.getAddress())
        .value(amount)
        .build();
  }

  public static Payment create(String destinationAddress, BigDecimal amount) {
    return Payment.builder()
        .to(destinationAddress)
        .value(amount)
        .build();
  }

  public static Payment create(String destinationAddress, BigDecimal amount, BigInteger fee) {
    return Payment.builder()
        .to(destinationAddress)
        .value(amount)
        .fee(fee)
        .build();
  }

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

  public static SubmittedPayment send(
      BlockchainWallet sourceWallet,
      String destinationAddress,
      BigDecimal amount
  ) throws SubmitPaymentException {
    return send(sourceWallet, destinationAddress, amount, null);
  }

  public static SubmittedPayment send(
      BlockchainWallet sourceWallet,
      String destinationAddress,
      BigDecimal amount,
      @Nullable String network
  ) throws SubmitPaymentException {
    return send(sourceWallet, destinationAddress, amount, null, network);
  }

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
