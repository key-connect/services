package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Specified network is not a valid network for the block chain")
public class UnknownNetworkException extends ResponseStatusException {

  public UnknownNetworkException(String chainId, String specifiedNetwork) {
    super(HttpStatus.BAD_REQUEST, specifiedNetwork + " is not a known network for blockchain " + chainId);
  }
}
