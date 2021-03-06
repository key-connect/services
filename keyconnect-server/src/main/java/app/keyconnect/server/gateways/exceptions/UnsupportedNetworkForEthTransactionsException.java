package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "We only support mainnet for viewing account transactions")
public class UnsupportedNetworkForEthTransactionsException extends
    ResponseStatusException {

  public UnsupportedNetworkForEthTransactionsException() {
    super(HttpStatus.BAD_REQUEST, "We only support mainnet for viewing account transactions");
  }

}
