package net.asany.jfantasy.framework.security.oauth2.core;

import net.asany.jfantasy.framework.security.AuthenticationException;

public class ClientRegistrationException extends AuthenticationException {

  public ClientRegistrationException(String msg) {
    super(msg);
  }
}
