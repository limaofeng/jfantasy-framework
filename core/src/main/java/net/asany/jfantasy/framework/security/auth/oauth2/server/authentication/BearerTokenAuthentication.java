package net.asany.jfantasy.framework.security.auth.oauth2.server.authentication;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

/**
 * Bearer Token 认证
 *
 * @author limaofeng
 */
public class BearerTokenAuthentication
    extends AbstractAuthTokenAuthenticationToken<AuthenticatedPrincipal, AuthToken> {

  private final Map<String, Object> attributes;

  public BearerTokenAuthentication(
      AuthenticatedPrincipal principal,
      AuthToken credentials,
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
