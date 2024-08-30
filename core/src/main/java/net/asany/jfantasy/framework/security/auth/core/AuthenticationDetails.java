package net.asany.jfantasy.framework.security.auth.core;

import java.time.Instant;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.TokenUsage;

public interface AuthenticationDetails {

  AuthType getAuthType();

  TokenUsage getTokenUsage();

  String getClientId();

  String getTenantId();

  ClientDetails getClientDetails();

  void setClientDetails(ClientDetails clientDetails);

  ClientSecret getClientSecret();

  void setClientSecret(ClientSecret clientSecret);

  Instant getExpiresAt();

  void setExpiresAt(Instant expiresAt);

  default AuthenticationDetails update(AuthenticationDetails details) {
    throw new UnsupportedOperationException("update");
  }
}
