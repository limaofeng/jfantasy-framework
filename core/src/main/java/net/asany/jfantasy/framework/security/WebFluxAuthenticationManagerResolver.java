package net.asany.jfantasy.framework.security;

import net.asany.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
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
