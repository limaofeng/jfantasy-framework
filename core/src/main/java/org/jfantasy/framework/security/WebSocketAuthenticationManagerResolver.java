package org.jfantasy.framework.security;

import javax.websocket.server.HandshakeRequest;
import org.jfantasy.framework.security.authentication.AuthenticationManagerResolver;

/**
 * AuthenticationManagerResolver
 *
 * @author limaofeng
 */
public class WebSocketAuthenticationManagerResolver
    implements AuthenticationManagerResolver<HandshakeRequest> {

  private final AuthenticationManager authenticationManager;

  public WebSocketAuthenticationManagerResolver(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public AuthenticationManager resolve(HandshakeRequest context) {
    return this.authenticationManager;
  }
}
