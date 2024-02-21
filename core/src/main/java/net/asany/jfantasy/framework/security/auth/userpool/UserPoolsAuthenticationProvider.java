package net.asany.jfantasy.framework.security.auth.userpool;

import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationProvider;

public class UserPoolsAuthenticationProvider
    implements AuthenticationProvider<BearerTokenAuthenticationToken> {

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return false;
  }

  @Override
  public Authentication authenticate(BearerTokenAuthenticationToken authentication)
      throws AuthenticationException {
    return null;
  }
}
