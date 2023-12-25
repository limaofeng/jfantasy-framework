package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.AuthenticationException;

public class NotAuthenticatedException extends AuthenticationException {

  public NotAuthenticatedException(String msg) {
    super(msg);
  }

  public NotAuthenticatedException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
