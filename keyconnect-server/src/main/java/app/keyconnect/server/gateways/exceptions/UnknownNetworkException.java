package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Specified network is not a valid network for the block chain")
public class UnknownNetworkException extends Exception {

  public UnknownNetworkException(String chainId, String specifiedNetwork) {
    super(specifiedNetwork + " is not a known network for blockchain " + chainId);
  }
}
