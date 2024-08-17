package net.asany.jfantasy.framework.security.auth.core;

import java.time.Instant;

public interface RefreshToken {
  String getClientId();

  String getTokenValue();

  Instant getIssuedAt();

  Instant getExpiresAt();
}
