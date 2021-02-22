package app.keyconnect.sdk.wallets;

public class SubmitPaymentException extends Exception {

  public SubmitPaymentException(String message) {
    super(message);
  }

  public SubmitPaymentException(String message, Throwable cause) {
    super(message, cause);
  }
}
