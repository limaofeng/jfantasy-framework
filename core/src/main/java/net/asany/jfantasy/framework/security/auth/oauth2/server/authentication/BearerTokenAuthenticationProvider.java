package net.asany.jfantasy.framework.security.auth.oauth2.server.authentication;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.auth.core.InvalidTokenException;
import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.springframework.core.annotation.Order;

/**
 * @author limaofeng
 */
@Slf4j
@Order(100)
public class BearerTokenAuthenticationProvider
    implements AuthenticationProvider<BearerTokenAuthenticationToken> {

  private final ResourceServerTokenServices<AuthToken> tokenServices;

  public BearerTokenAuthenticationProvider(ResourceServerTokenServices<AuthToken> tokenServices) {
    this.tokenServices = tokenServices;
  }

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(BearerTokenAuthenticationToken bearer)
      throws AuthenticationException {
    AbstractAuthenticationToken token = this.tokenServices.loadAuthentication(bearer);
    if (token == null) {
      throw new InvalidTokenException("Invalid token");
    }
    token.setDetails(bearer.getDetails());
    log.debug("Authenticated token");
    return token;
  }
}
