package net.asany.jfantasy.framework.security.auth.apikey;

import java.time.Instant;
import net.asany.jfantasy.framework.security.auth.core.AbstractAuthToken;

public class ApiKey extends AbstractAuthToken {

  protected ApiKey(String clientId, String tokenValue, Instant issuedAt, Instant expiresAt) {
    super(clientId, tokenValue, issuedAt, expiresAt);
  }
}
