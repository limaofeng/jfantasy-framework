package org.jfantasy.framework.security.oauth2.server.authentication;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

/** @author limaofeng */
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
