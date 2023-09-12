package org.jfantasy.framework.security;

import org.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
import org.springframework.web.reactive.function.server.ServerRequest;

public class WebFluxAuthenticationManagerResolver
    implements AuthenticationManagerResolver<ServerRequest> {

  private final AuthenticationManager authenticationManager;

  public WebFluxAuthenticationManagerResolver(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public AuthenticationManager resolve(ServerRequest context) {
    return this.authenticationManager;
  }
}
