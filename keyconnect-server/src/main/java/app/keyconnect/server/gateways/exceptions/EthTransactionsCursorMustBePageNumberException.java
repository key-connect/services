package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EthTransactionsCursorMustBePageNumberException extends
    ResponseStatusException {

  public EthTransactionsCursorMustBePageNumberException() {
    super(HttpStatus.BAD_REQUEST, "Value of cursor must be a valid integer");
  }

}
