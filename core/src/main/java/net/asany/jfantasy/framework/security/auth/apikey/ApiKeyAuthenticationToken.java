package net.asany.jfantasy.framework.security.auth.apikey;

import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;

public class ApiKeyAuthenticationToken extends BearerTokenAuthenticationToken {
  public ApiKeyAuthenticationToken(String token) {
    super(TokenType.API_KEY, token);
  }
}
