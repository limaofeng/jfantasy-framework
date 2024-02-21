package net.asany.jfantasy.framework.security.auth.apikey;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.auth.core.InvalidTokenException;
import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(10)
public class ApiKeyAuthenticationProvider
    implements AuthenticationProvider<ApiKeyAuthenticationToken> {

  private final ResourceServerTokenServices<ApiKey> tokenServices;

  public ApiKeyAuthenticationProvider(ResourceServerTokenServices<ApiKey> tokenServices) {
    this.tokenServices = tokenServices;
  }

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(ApiKeyAuthenticationToken authentication)
      throws AuthenticationException {
    AbstractAuthenticationToken token = this.tokenServices.loadAuthentication(authentication);
    if (token == null) {
      throw new InvalidTokenException("Invalid token");
    }
    token.setDetails(authentication.getDetails());
    log.debug("Authenticated token");
    return token;
  }
}
