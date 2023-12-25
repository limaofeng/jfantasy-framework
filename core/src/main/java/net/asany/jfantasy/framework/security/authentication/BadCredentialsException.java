package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.AuthenticationException;

public class BadCredentialsException extends AuthenticationException {

  public BadCredentialsException(String message) {
    super(message);
  }
}
