package net.asany.jfantasy.framework.security.web;

import jakarta.websocket.server.HandshakeRequest;
import net.asany.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import net.asany.jfantasy.framework.security.web.authentication.WebSocketAuthenticationDetails;

/**
 * WebSocketAuthenticationDetailsSource
 *
 * @author limaofeng
 */
public class WebSocketAuthenticationDetailsSource
    implements AuthenticationDetailsSource<HandshakeRequest, WebSocketAuthenticationDetails> {

  @Override
  public WebSocketAuthenticationDetails buildDetails(HandshakeRequest handshakeRequest) {
    return new WebSocketAuthenticationDetails(handshakeRequest);
  }
}
