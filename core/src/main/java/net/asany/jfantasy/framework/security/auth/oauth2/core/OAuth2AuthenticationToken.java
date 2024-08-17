package net.asany.jfantasy.framework.security.auth.oauth2.core;

import java.util.Collections;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.core.AuthorizationGrantType;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;

/**
 * OAuth 身份验证
 *
 * <p>用于 OAuth2 认证的身份验证令牌≤/p>
 */
@Getter
public class OAuth2AuthenticationToken extends AbstractAuthenticationToken<String> {

  private final String principal;
  private final String username;
  private final String password;
  private Object credentials;
  private final AuthorizationGrantType grantType;

  public OAuth2AuthenticationToken(OAuth2AuthenticationDetails details) {
    super(Collections.emptyList());
    this.grantType = details.getGrantType();
    this.username = details.getUsername();
    this.password = details.getPassword();
    this.principal = details.getClientId();
    setDetails(details);
    setAuthenticated(false);
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
