package net.asany.jfantasy.framework.security.auth.oauth2.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.auth.core.AbstractAuthToken;
import org.springframework.util.Assert;

@Getter
public class OAuth2AccessToken extends AbstractAuthToken {

  private TokenType tokenType = TokenType.JWT;

  public OAuth2AccessToken(
      String clientId, String tokenValue, Instant issuedAt, Instant expiresAt) {
    this(clientId, tokenValue, issuedAt, expiresAt, Collections.emptySet());
  }

  public OAuth2AccessToken(
      String clientId, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
    super(clientId, tokenValue, issuedAt, expiresAt, scopes);
  }

  public OAuth2AccessToken(
      String clientId,
      TokenType tokenType,
      String tokenValue,
      Instant issuedAt,
      Instant expiresAt) {
    this(clientId, tokenValue, issuedAt, expiresAt);
    Assert.notNull(tokenType, "tokenType cannot be null");
    this.tokenType = tokenType;
  }

  @JsonCreator
  public OAuth2AccessToken(
      @JsonProperty("client_id") String clientId,
      @JsonProperty("token_type") TokenType tokenType,
      @JsonProperty("token_value") String tokenValue,
      @JsonProperty("refresh_token_value") String refreshTokenValue,
      @JsonProperty("issued_at") Instant issuedAt,
      @JsonProperty("expires_at") Instant expiresAt,
      @JsonProperty("token") Set<String> scopes) {
    super(clientId, tokenValue, refreshTokenValue, issuedAt, expiresAt, scopes);
    Assert.notNull(tokenType, "tokenType cannot be null");
    this.tokenType = tokenType;
  }

  @Override
  public TokenType getTokenType() {
    return tokenType;
  }
}
