package net.asany.jfantasy.framework.security.auth.apikey;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.AbstractAuthTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

public class ApiKeyAuthentication
    extends AbstractAuthTokenAuthenticationToken<AuthenticatedPrincipal, ApiKey> {

  private final Map<String, Object> attributes;

  public ApiKeyAuthentication(
      AuthenticatedPrincipal principal,
      ApiKey credentials,
      Collection<GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
    this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(principal.getAttributes()));
    setAuthenticated(true);
  }

  @Override
  public Map<String, Object> getTokenAttributes() {
    return this.attributes;
  }
}
