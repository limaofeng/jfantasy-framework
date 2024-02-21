package net.asany.jfantasy.framework.security.auth.core;

import java.time.Instant;
import net.asany.jfantasy.framework.security.auth.TokenType;

public class AuthRefreshToken extends AbstractAuthToken {

  public AuthRefreshToken(String clientId, String tokenValue, Instant issuedAt) {
    this(clientId, tokenValue, issuedAt, null);
  }

  public AuthRefreshToken(String clientId, String tokenValue, Instant issuedAt, Instant expiresAt) {
    super(clientId, tokenValue, issuedAt, expiresAt);
  }

  @Override
  public TokenType getTokenType() {
    return null;
  }
}
