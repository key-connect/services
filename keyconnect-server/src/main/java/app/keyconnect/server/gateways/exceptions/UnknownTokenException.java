package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Specified token is not a valid token for the block chain")
public class UnknownTokenException extends Exception {

  public UnknownTokenException(String chainId, String specifiedToken) {
    super(specifiedToken + " is not a known token for blockchain " + chainId);
  }
}
