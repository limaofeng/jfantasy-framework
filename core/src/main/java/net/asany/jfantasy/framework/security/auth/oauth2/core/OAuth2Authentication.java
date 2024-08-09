package net.asany.jfantasy.framework.security.auth.oauth2.core;

import lombok.Setter;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;

/**
 * OAuth 身份验证
 *
 * @author limaofeng
 */
public class OAuth2Authentication extends AbstractAuthenticationToken {

  private final OAuth2AuthenticatedPrincipal principal;
  @Setter private Object credentials;

  public OAuth2Authentication(Authentication authentication, OAuth2AuthenticationDetails details) {
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
    return (T) this.principal;
  }
}
