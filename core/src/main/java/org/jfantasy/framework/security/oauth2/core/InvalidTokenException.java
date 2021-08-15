package org.jfantasy.framework.security.oauth2.core;

import org.jfantasy.framework.security.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

  public InvalidTokenException(String msg) {
    super(msg);
  }
}
