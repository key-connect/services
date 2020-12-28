package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Value of cursor must be a valid integer")
public class EthTransactionsCursorMustBePageNumberException extends
    RuntimeException {

}
