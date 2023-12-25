package net.asany.jfantasy.framework.spring.mvc.error;

import java.io.Serializable;
import lombok.Getter;
import net.asany.jfantasy.framework.error.ValidationException;
import org.springframework.http.HttpStatus;

@Getter
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

  public void setState(Serializable state) {
    this.state = state;
  }
}
