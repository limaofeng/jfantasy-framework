package org.jfantasy.framework.spring.mvc.error;

import java.io.Serializable;
import org.jfantasy.framework.error.ValidationException;
import org.springframework.http.HttpStatus;

public class RestException extends ValidationException {

  private int statusCode = HttpStatus.BAD_REQUEST.value();
  private Serializable state;

  public RestException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public RestException(int statusCode, String code, String message) {
    super(code, message);
    this.statusCode = statusCode;
  }

  public RestException(String message) {
    super(message);
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Serializable getState() {
    return state;
  }

  public void setState(Serializable state) {
    this.state = state;
  }
}
