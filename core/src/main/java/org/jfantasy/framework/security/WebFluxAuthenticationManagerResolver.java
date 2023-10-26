package org.jfantasy.framework.security;

import org.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class WebFluxAuthenticationManagerResolver
    implements AuthenticationManagerResolver<ServerHttpRequest> {

  private final AuthenticationManager authenticationManager;

  public WebFluxAuthenticationManagerResolver(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public AuthenticationManager resolve(ServerHttpRequest context) {
    return this.authenticationManager;
  }
}
