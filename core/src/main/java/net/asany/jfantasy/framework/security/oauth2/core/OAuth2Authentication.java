package net.asany.jfantasy.framework.security.oauth2.core;

import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;

/**
 * OAuth 身份验证
 *
 * @author limaofeng
 */
public class OAuth2Authentication extends AbstractAuthenticationToken {

  private final OAuth2AuthenticatedPrincipal principal;
  private Object credentials;

  public OAuth2Authentication(Authentication authentication, OAuth2AuthenticationDetails details) {
    super(authentication.getAuthorities());
    this.credentials = authentication.getCredentials();
    this.principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
    setDetails(details);
    setAuthenticated(authentication.isAuthenticated());
  }

  public void setCredentials(Object credentials) {
    this.credentials = credentials;
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  @Override
  public Object getPrincipal() {
    return this.principal;
  }
}
