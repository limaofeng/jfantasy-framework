package org.jfantasy.framework.security.web;

import javax.websocket.server.HandshakeRequest;
import org.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import org.jfantasy.framework.security.web.authentication.WebSocketAuthenticationDetails;

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
