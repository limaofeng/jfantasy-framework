package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.AuthenticationException;

public class ProviderNotFoundException extends AuthenticationException {

  public ProviderNotFoundException(String msg) {
    super(msg);
  }
}
