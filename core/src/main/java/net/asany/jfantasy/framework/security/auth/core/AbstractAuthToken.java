package net.asany.jfantasy.framework.security.auth.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import lombok.Data;
import org.springframework.util.Assert;

@Data
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

  @Override
  public String getClientId() {
    return this.clientId;
  }

  @Override
  public String getTokenValue() {
    return this.tokenValue;
  }

  @Override
  public Instant getIssuedAt() {
    return this.issuedAt;
  }

  @Override
  public Set<String> getScopes() {
    return this.scopes;
  }

  @Override
  public Instant getExpiresAt() {
    return this.expiresAt;
  }

  @Override
  public String getRefreshTokenValue() {
    return refreshTokenValue;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    AbstractAuthToken other = (AbstractAuthToken) obj;
    if (!this.getTokenValue().equals(other.getTokenValue())) {
      return false;
    }
    if ((this.getIssuedAt() != null)
        ? !this.getIssuedAt().equals(other.getIssuedAt())
        : other.getIssuedAt() != null) {
      return false;
    }
    return (this.getExpiresAt() != null)
        ? this.getExpiresAt().equals(other.getExpiresAt())
        : other.getExpiresAt() == null;
  }

  @Override
  public int hashCode() {
    int result = this.getTokenValue().hashCode();
    result = 31 * result + ((this.getIssuedAt() != null) ? this.getIssuedAt().hashCode() : 0);
    result = 31 * result + ((this.getExpiresAt() != null) ? this.getExpiresAt().hashCode() : 0);
    return result;
  }
}
