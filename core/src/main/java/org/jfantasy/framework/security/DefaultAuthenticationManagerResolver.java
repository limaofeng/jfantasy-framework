package org.jfantasy.framework.security;

import javax.servlet.http.HttpServletRequest;
import org.jfantasy.framework.security.authentication.AuthenticationManagerResolver;

/**
 * AuthenticationManagerResolver
 *
 * @author limaofeng
 */
public class DefaultAuthenticationManagerResolver
    implements AuthenticationManagerResolver<HttpServletRequest> {

  private final AuthenticationManager authenticationManager;

  public DefaultAuthenticationManagerResolver(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public AuthenticationManager resolve(HttpServletRequest context) {
    return this.authenticationManager;
  }
}
