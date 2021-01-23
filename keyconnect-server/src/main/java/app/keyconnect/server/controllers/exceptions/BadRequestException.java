package app.keyconnect.server.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {

  public BadRequestException(Throwable cause) {
    super(HttpStatus.BAD_REQUEST, cause.getMessage(), cause);
  }

  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
