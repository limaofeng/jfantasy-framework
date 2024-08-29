package net.asany.jfantasy.framework.security.auth.oauth2.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.auth.core.AuthorizationGrantType;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.AbstractAuthTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

public class OAuth2Authentication
    extends AbstractAuthTokenAuthenticationToken<AuthenticatedPrincipal, AuthToken> {
  @JsonProperty("grant_type")
  private AuthorizationGrantType grantType;

  private final Map<String, Object> attributes;

  public OAuth2Authentication(
      AuthorizationGrantType grantType,
      AuthenticatedPrincipal principal,
      AuthToken credentials,
      Collection<GrantedAuthority> authorities,
      AuthenticationDetails details) {
    super(principal, credentials, authorities);
    this.grantType = grantType;
    this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(principal.getAttributes()));
    setAuthenticated(true);
    this.details = details;
  }

  @Override
  public Map<String, Object> getTokenAttributes() {
    return this.attributes;
  }
}
