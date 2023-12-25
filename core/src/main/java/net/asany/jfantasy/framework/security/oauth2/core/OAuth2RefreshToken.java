package net.asany.jfantasy.framework.security.oauth2.core;

import java.time.Instant;

public class OAuth2RefreshToken extends AbstractOAuth2Token {

  public OAuth2RefreshToken(String tokenValue, Instant issuedAt) {
    this(tokenValue, issuedAt, null);
  }

  public OAuth2RefreshToken(String tokenValue, Instant issuedAt, Instant expiresAt) {
    super(tokenValue, issuedAt, expiresAt);
  }
}
