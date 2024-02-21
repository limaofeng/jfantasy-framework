package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

public class TokenAuthenticatedPrincipal implements AuthenticatedPrincipal {

  private final String token;

  public TokenAuthenticatedPrincipal(String token) {
    this.token = token;
  }

  @Override
  public String getName() {
    return this.token;
  }
}
