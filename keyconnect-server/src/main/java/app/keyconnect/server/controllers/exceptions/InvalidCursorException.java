package app.keyconnect.server.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Provided cursor was not valid")
public class InvalidCursorException extends RuntimeException {

}
