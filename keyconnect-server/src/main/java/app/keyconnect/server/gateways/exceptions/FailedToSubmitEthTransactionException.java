package app.keyconnect.server.gateways.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unable to send transaction")
public class FailedToSubmitEthTransactionException extends
    RuntimeException {

}
