package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnknownTokenException extends ResponseStatusException {

  public UnknownTokenException(String chainId, String specifiedToken) {
    super(HttpStatus.BAD_REQUEST, specifiedToken + " is not a known token for blockchain " + chainId);
  }
}
