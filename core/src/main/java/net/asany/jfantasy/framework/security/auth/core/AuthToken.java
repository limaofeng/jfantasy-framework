package net.asany.jfantasy.framework.security.auth.core;

import java.time.Instant;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

public interface AuthToken {

  TokenType getTokenType();

  String getClientId();

  String getTokenValue();

  String getRefreshTokenValue();

  default Instant getIssuedAt() {
    return null;
  }

  default Instant getExpiresAt() {
    return null;
  }

  default Class<? extends AuthenticatedPrincipal> getPrincipalType() {
    return LoginUser.class;
  }
}
