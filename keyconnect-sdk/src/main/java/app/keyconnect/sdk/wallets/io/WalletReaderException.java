package app.keyconnect.sdk.wallets.io;

public class WalletReaderException extends RuntimeException {

  public WalletReaderException(String message) {
    super(message);
  }

  public WalletReaderException(String message, Throwable cause) {
    super(message, cause);
  }
}
