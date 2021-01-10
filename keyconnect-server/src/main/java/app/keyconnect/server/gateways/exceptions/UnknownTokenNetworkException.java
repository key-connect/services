package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Specified token is not a valid token for the block chain")
public class UnknownTokenNetworkException extends Exception {

  public UnknownTokenNetworkException(String chainId, String specifiedToken, String specifiedNetwork) {
    super(specifiedNetwork + " is not a known network for token " + specifiedToken + " on blockchain " + chainId);
  }
}
