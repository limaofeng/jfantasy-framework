package org.jfantasy.framework.security.oauth2.server.authentication;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.jfantasy.framework.security.oauth2.core.InvalidTokenException;
import org.jfantasy.framework.security.oauth2.core.token.ResourceServerTokenServices;
import org.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;

/** @author limaofeng */
@Slf4j
public class BearerTokenAuthenticationProvider
    implements AuthenticationProvider<BearerTokenAuthenticationToken> {

  private ResourceServerTokenServices tokenServices;

  public BearerTokenAuthenticationProvider(ResourceServerTokenServices tokenServices) {
    this.tokenServices = tokenServices;
  }

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(BearerTokenAuthenticationToken bearer)
      throws AuthenticationException {
    AbstractAuthenticationToken token = this.tokenServices.loadAuthentication(bearer.getToken());
    if (token == null) {
      throw new InvalidTokenException("Invalid token");
    }
    token.setDetails(bearer.getDetails());
    log.debug("Authenticated token");
    return token;
  }
}
