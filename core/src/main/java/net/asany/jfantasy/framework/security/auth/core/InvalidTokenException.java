package net.asany.jfantasy.framework.security.auth.core;

import net.asany.jfantasy.framework.security.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

  public InvalidTokenException(String msg) {
    super(msg);
  }
}
