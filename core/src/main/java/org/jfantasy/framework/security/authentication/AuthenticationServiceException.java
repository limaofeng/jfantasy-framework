package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationException;

public class AuthenticationServiceException extends AuthenticationException {

  public AuthenticationServiceException(String msg) {
    super(msg);
  }

  public AuthenticationServiceException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
