package net.asany.jfantasy.framework.security.auth.oauth2.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.auth.core.AbstractAuthToken;
import org.springframework.util.Assert;

@Getter
public class OAuth2AccessToken extends AbstractAuthToken {

  private final Set<String> scopes;

  public OAuth2AccessToken(
      String clientId, String tokenValue, Instant issuedAt, Instant expiresAt) {
    this(clientId, tokenValue, issuedAt, expiresAt, Collections.emptySet());
  }

  public OAuth2AccessToken(
      String clientId, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
    super(clientId, tokenValue, issuedAt, expiresAt);
    this.scopes = Collections.unmodifiableSet((scopes != null) ? scopes : Collections.emptySet());
  }

  public OAuth2AccessToken(
      TokenType tokenType,
      String tokenValue,
      String refreshTokenValue,
      Instant issuedAt,
      Instant expiresAt,
      Set<String> scopes) {
    super(tokenValue, refreshTokenValue, issuedAt, expiresAt);
    Assert.notNull(tokenType, "tokenType cannot be null");
    this.scopes = Collections.unmodifiableSet((scopes != null) ? scopes : Collections.emptySet());
  }

  @Override
  public TokenType getTokenType() {
    return TokenType.JWT;
  }
}
