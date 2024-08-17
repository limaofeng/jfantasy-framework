package net.asany.jfantasy.framework.security.auth.core;

import java.time.Instant;

public class AuthRefreshToken extends AbstractAuthToken implements RefreshToken {

  public AuthRefreshToken(String clientId, String tokenValue, Instant issuedAt) {
    this(clientId, tokenValue, issuedAt, null);
  }

  public AuthRefreshToken(String clientId, String tokenValue, Instant issuedAt, Instant expiresAt) {
    super(clientId, tokenValue, issuedAt, expiresAt);
  }
}
