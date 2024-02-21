package net.asany.jfantasy.framework.security.auth.apikey;

import lombok.Setter;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {
  private final AuthenticatedPrincipal principal;
  @Setter private Object credentials;

  public ApiKeyAuthentication(Authentication authentication, AuthenticationDetails details) {
    super(authentication.getAuthorities());
    this.credentials = authentication.getCredentials();
    this.principal = authentication.getPrincipal();
    setDetails(details);
    setAuthenticated(authentication.isAuthenticated());
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  @Override
  public <T> T getPrincipal() {
    //noinspection unchecked
    return (T) this.principal;
  }
}
