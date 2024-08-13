package net.asany.jfantasy.framework.security.auth.core;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

public interface AuthToken {

  TokenType getTokenType();

  String getClientId();

  String getTokenValue();

  String getRefreshTokenValue();

  Instant getIssuedAt();

  Instant getExpiresAt();

  Set<String> getScopes();

  default boolean isExpired() {
    return getExpiresAt() != null && Instant.now().isAfter(getExpiresAt());
  }

  default Class<? extends AuthenticatedPrincipal> getPrincipalType() {
    return LoginUser.class;
  }

  default long getExpiresIn() {
    return Duration.between(getIssuedAt(), getExpiresAt()).getSeconds();
  }
}
