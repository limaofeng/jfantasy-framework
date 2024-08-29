package net.asany.jfantasy.framework.security.auth.oauth2.core;

import net.asany.jfantasy.framework.security.auth.core.AbstractAuthToken;
import net.asany.jfantasy.framework.security.auth.core.ClientSecret;

public class OAuth2Token extends AbstractAuthToken {

  private final ClientSecret clientSecret;

  public OAuth2Token(ClientSecret clientSecret) {
    this.clientSecret = clientSecret;
  }
}
