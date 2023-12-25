package net.asany.jfantasy.framework.security.oauth2.server.authentication;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

/**
 * Bearer Token 认证
 *
 * @author limaofeng
 */
public class BearerTokenAuthentication
    extends AbstractOAuth2TokenAuthenticationToken<OAuth2AccessToken> {

  private final Map<String, Object> attributes;

  public BearerTokenAuthentication(
      OAuth2AuthenticatedPrincipal principal,
      OAuth2AccessToken credentials,
      Collection<GrantedAuthority> authorities) {
    super(credentials, principal, credentials, authorities);
    this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(principal.getAttributes()));
    setAuthenticated(true);
  }

  @Override
  public Map<String, Object> getTokenAttributes() {
    return this.attributes;
  }
}
