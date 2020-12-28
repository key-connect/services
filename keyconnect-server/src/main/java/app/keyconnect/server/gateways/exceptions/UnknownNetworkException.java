package app.keyconnect.server.gateways.exceptions;

public class UnknownNetworkException extends Exception {

  public UnknownNetworkException(String chainId, String specifiedNetwork) {
    super(specifiedNetwork + " is not a known network for blockchain " + chainId);
  }
}
