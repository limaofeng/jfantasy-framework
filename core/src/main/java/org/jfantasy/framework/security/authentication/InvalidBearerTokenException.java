package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationException;

/**
 * @author limaofeng
 */
public class InvalidBearerTokenException extends AuthenticationException {

  public InvalidBearerTokenException(String message) {
    super(message);
  }
}
