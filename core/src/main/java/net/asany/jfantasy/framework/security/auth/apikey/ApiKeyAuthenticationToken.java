package net.asany.jfantasy.framework.security.auth.apikey;

import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;

public class ApiKeyAuthenticationToken extends BearerTokenAuthenticationToken {
  public ApiKeyAuthenticationToken(String token) {
    super(AuthType.API_KEY, token);
  }

  public ApiKeyAuthenticationToken(String token, AuthenticationDetails details) {
    super(AuthType.API_KEY, token);
    this.details = details;
  }
}
