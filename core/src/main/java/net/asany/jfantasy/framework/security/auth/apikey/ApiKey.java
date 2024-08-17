package net.asany.jfantasy.framework.security.auth.apikey;

import java.time.Instant;
import net.asany.jfantasy.framework.security.auth.core.AbstractAuthToken;
import net.asany.jfantasy.framework.security.authentication.ApiKeyPrincipal;

public class ApiKey extends AbstractAuthToken {

  protected ApiKey(String clientId, String tokenValue, Instant issuedAt, Instant expiresAt) {
    super(clientId, tokenValue, issuedAt, expiresAt);
  }

  @Override
  public Class<ApiKeyPrincipal> getPrincipalType() {
    return ApiKeyPrincipal.class;
  }
}
