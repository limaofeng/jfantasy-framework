package org.jfantasy.framework.spring.mvc.error;

import org.springframework.http.HttpStatus;

/**
 * @author limaofeng
 */
public class SecurityException extends RestException {

  public SecurityException(String code, String message) {
    super(HttpStatus.FORBIDDEN.value(), code, message);
  }

  public SecurityException(String message) {
    super(message);
  }
}
