package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnknownTokenNetworkException extends ResponseStatusException {

  public UnknownTokenNetworkException(String chainId, String specifiedToken, String specifiedNetwork) {
    super(HttpStatus.BAD_REQUEST, specifiedNetwork + " is not a known network for token " + specifiedToken + " on blockchain " + chainId);
  }
}
