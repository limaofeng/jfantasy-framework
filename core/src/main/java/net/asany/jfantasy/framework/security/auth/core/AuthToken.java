package net.asany.jfantasy.framework.security.auth.core;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

public interface AuthToken {

  String getClientId();

  String getTokenValue();

  Instant getIssuedAt();

  Instant getExpiresAt();

  Set<String> getScopes();

  default boolean isExpired() {
    return getExpiresAt() != null && Instant.now().isAfter(getExpiresAt());
  }

  default Long getExpiresIn() {
    if (getExpiresAt() == null) {
      return null;
    }
    return Duration.between(getIssuedAt(), getExpiresAt()).getSeconds();
  }
}
