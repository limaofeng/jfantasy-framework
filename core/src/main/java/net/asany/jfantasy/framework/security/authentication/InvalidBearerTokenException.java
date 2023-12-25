package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.AuthenticationException;

/**
 * @author limaofeng
 */
public class InvalidBearerTokenException extends AuthenticationException {

  public InvalidBearerTokenException(String message) {
    super(message);
  }
}
