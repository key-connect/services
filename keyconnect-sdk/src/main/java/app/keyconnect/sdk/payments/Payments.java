package app.keyconnect.sdk.payments;

import app.keyconnect.api.client.BlockchainsApi;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import java.math.BigDecimal;

public class Payments {

  public static Payment create(String destinationAddress, BigDecimal amount) {
    return Payment.builder()
        .to(destinationAddress)
        .value(amount)
        .build();
  }

  public static SubmittedPayment send(
      String destinationAddress,
      BigDecimal amount,
      BlockchainWallet wallet,
      String network
  ) throws SubmitPaymentException {
    return send(destinationAddress, amount, wallet, null, network);
  }

  public static SubmittedPayment send(
      String destinationAddress,
      BigDecimal amount,
      BlockchainWallet wallet,
      BlockchainsApi blockchainsApi,
      String network
  ) throws SubmitPaymentException {
    final SignedPayment signedPayment = create(destinationAddress, amount)
        .sign(wallet);

    if (null == blockchainsApi)
        return signedPayment.submit(network);
    else
      return signedPayment.submit(blockchainsApi, network);
  }
}
