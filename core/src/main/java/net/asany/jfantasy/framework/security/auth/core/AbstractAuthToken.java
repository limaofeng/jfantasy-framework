package net.asany.jfantasy.framework.security.auth.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;

@Data
@EqualsAndHashCode(of = "tokenValue")
public abstract class AbstractAuthToken implements AuthToken {

  private String clientId;
  private String tokenValue;
  private Instant issuedAt;
  private Instant expiresAt;
  private String refreshTokenValue;
  private Set<String> scopes;

  public AbstractAuthToken() {}

  protected AbstractAuthToken(String clientId, String tokenValue) {
    this(clientId, tokenValue, null, null, null);
  }

  protected AbstractAuthToken(
      String clientId, String tokenValue, Instant issuedAt, Instant expiresAt) {
    this(clientId, tokenValue, issuedAt, expiresAt, null);
  }

  protected AbstractAuthToken(
      String clientId, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
    Assert.hasText(clientId, "clientId cannot be empty");
    Assert.hasText(tokenValue, "tokenValue cannot be empty");
    if (issuedAt != null && expiresAt != null) {
      Assert.isTrue(expiresAt.isAfter(issuedAt), "expiresAt must be after issuedAt");
    }
    this.clientId = clientId;
    this.tokenValue = tokenValue;
    this.issuedAt = issuedAt;
    this.expiresAt = expiresAt;
    this.scopes = Collections.unmodifiableSet((scopes != null) ? scopes : Collections.emptySet());
  }

  protected AbstractAuthToken(String clientId, String tokenValue, String refreshTokenValue) {
    this(clientId, tokenValue, refreshTokenValue, null, null, null);
  }

  protected AbstractAuthToken(
      String clientId,
      String tokenValue,
      String refreshTokenValue,
      Instant issuedAt,
      Instant expiresAt,
      Set<String> scopes) {
    Assert.hasText(tokenValue, "tokenValue cannot be empty");
    if (issuedAt != null && expiresAt != null) {
      Assert.isTrue(expiresAt.isAfter(issuedAt), "expiresAt must be after issuedAt");
    }
    this.clientId = clientId;
    this.refreshTokenValue = refreshTokenValue;
    this.tokenValue = tokenValue;
    this.issuedAt = issuedAt;
    this.expiresAt = expiresAt;
    this.scopes = Collections.unmodifiableSet((scopes != null) ? scopes : Collections.emptySet());
  }
}
