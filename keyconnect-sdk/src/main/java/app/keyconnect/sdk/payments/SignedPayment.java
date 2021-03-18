package app.keyconnect.sdk.payments;

import app.keyconnect.api.ApiException;
import app.keyconnect.api.client.BlockchainsApi;
import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.client.model.BlockchainFee;
import app.keyconnect.api.client.model.SubmitTransactionRequest;
import app.keyconnect.api.client.model.SubmitTransactionResult;
import app.keyconnect.sdk.api.KeyConnectApiFactory;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import java.math.BigInteger;
import java.util.Locale;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a signed {@link Payment} object. This object does not actually contain a signed
 * payment but contains the necessary tools that can be used to sign a payment.
 */
@Builder
@Getter
public class SignedPayment {

  private final BlockchainWallet wallet;
  private final Payment payment;

  /**
   * Submits this {@link SignedPayment} object to the blockchain.
   * Any fees, nonce etc are calculated for the default network (usually mainnet) (unless specified
   * in the underlying {@link Payment} object).
   * Uses the provided {@link BlockchainsApi} instance to submit the signed payment.
   * @param blockchainsApi {@link BlockchainsApi} instance to reuse.
   * @return {@link SubmittedPayment} object indicating a submitted payment
   * @throws SubmitPaymentException when there is a problem with submitting the payment.
   */
  public SubmittedPayment submit(BlockchainsApi blockchainsApi) throws SubmitPaymentException {
    return submit(blockchainsApi, null);
  }

  /**
   * Submits this {@link SignedPayment} object to the blockchain.
   * Any fees, nonce etc are calculated for the default network (usually mainnet) (unless specified
   * in the underlying {@link Payment} object).
   * @return {@link SubmittedPayment} object indicating a submitted payment
   * @throws SubmitPaymentException when there is a problem with submitting the payment.
   */
  public SubmittedPayment submit() throws SubmitPaymentException {
    return submit(KeyConnectApiFactory.getInstance().getDefaultBlockchainsApi(), null);
  }

  /**
   * Submits this {@link SignedPayment} object to the specified blockchain network (nullable).
   * Any fees, nonce etc are calculated for the specified network (unless specified in the
   * underlying {@link Payment} object).
   * @param network Blockchain network to submit the payment to. Can be null.
   * @return {@link SubmittedPayment} object indicating a submitted payment
   * @throws SubmitPaymentException when there is a problem with submitting the payment.
   */
  public SubmittedPayment submit(@Nullable String network) throws SubmitPaymentException {
    return submit(KeyConnectApiFactory.getInstance().getDefaultBlockchainsApi(), network);
  }

  /**
   * Submits this {@link SignedPayment} object to the specified blockchain network (nullable).
   * Any fees, nonce etc are calculated for the specified network (unless specified in the
   * underlying {@link Payment} object.
   * Uses the provided {@link BlockchainsApi} instance to submit the signed payment.
   * @param api {@link BlockchainsApi} instance to reuse.
   * @param network Blockchain network to submit the payment to. Can be null.
   * @return {@link SubmittedPayment} object indicating a submitted payment
   * @throws SubmitPaymentException when there is a problem with submitting the payment.
   */
  public SubmittedPayment submit(BlockchainsApi api, @Nullable String network)
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
