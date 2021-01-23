package app.keyconnect.server.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

  public BadRequestException(Throwable cause) {
    super(cause.getMessage(), cause);
  }

  public BadRequestException(String message) {
    super(message);
  }

  @Override
  @ResponseBody
  public String getMessage() {
    return super.getMessage();
  }
}
