package app.keyconnect.sdk.payments;

import app.keyconnect.api.ApiClient;
import app.keyconnect.api.ApiException;
import app.keyconnect.api.client.BlockchainsApi;
import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainFee;
import app.keyconnect.api.client.model.SubmitTransactionRequest;
import app.keyconnect.api.client.model.SubmitTransactionResult;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import java.math.BigInteger;
import java.util.Locale;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Builder
@Getter
public class SignedPayment {

  private BlockchainWallet wallet;
  private Payment payment;

  public SubmittedPayment submit(String network) throws SubmitPaymentException {
    return submit(new BlockchainsApi(new ApiClient()), network);
  }

  public SubmittedPayment submit(BlockchainsApi api, String network)
      throws SubmitPaymentException {
    final String chainId = wallet.getChainId().name().toLowerCase(Locale.ROOT);

    try {
      BigInteger fee = payment.getFee();
      if (fee == null || fee.compareTo(BigInteger.ZERO) == 0) {
        // fee is not set, lets get it
        final BlockchainFee feeResponse = api.getFee(chainId, network);
        if (feeResponse == null
            || feeResponse.getFee() == null
            || StringUtils.isBlank(feeResponse.getFee().getAmount())) {
          throw new SubmitPaymentException(
              "Failed to get fee during payment submit because it was not set");
        }
        fee = new BigInteger(feeResponse.getFee().getAmount());
      }

      long nonce = payment.getNonce();
      if (nonce == 0 || nonce == -1) {
        final BlockchainAccountInfo accountInfo = api
            .getAccountInfo(chainId, wallet.getAddress(), network, null);
        if (accountInfo == null
            || StringUtils.isBlank(accountInfo.getNonce())) {
          throw new SubmitPaymentException(
              "Failed to get account nonce/sequence because it was not set");
        }

        nonce = Long.parseLong(accountInfo.getNonce());
      }

      final String signedPayment = wallet
          .buildPaymentTransaction(payment.getTo(), payment.getValue(), fee, nonce);
      final SubmitTransactionRequest submitTransactionRequest = new SubmitTransactionRequest()
          .transaction(signedPayment);

      final SubmitTransactionResult result = api
          .submitTransaction(chainId, network, submitTransactionRequest);

      if (null == result.getTransaction() || StringUtils
          .isBlank(result.getTransaction().getHash())) {
        throw new SubmitPaymentException("Unable to submit payment");
      }

      return new SubmittedPayment(result.getTransaction());
    } catch (ApiException e) {
      throw new SubmitPaymentException("Unable to get fee", e);
    }
  }
}
