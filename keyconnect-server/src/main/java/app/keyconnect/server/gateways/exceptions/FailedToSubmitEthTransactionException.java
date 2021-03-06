package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FailedToSubmitEthTransactionException extends
    ResponseStatusException {

  public FailedToSubmitEthTransactionException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send transaction");
  }

}
