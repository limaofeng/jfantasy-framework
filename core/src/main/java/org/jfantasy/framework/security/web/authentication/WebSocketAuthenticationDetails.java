package org.jfantasy.framework.security.web.authentication;

import javax.websocket.server.HandshakeRequest;

/**
 * WebSocketAuthenticationDetails
 *
 * @author limaofeng
 */
public class WebSocketAuthenticationDetails {

  private HandshakeRequest handshakeRequest;

  public WebSocketAuthenticationDetails(HandshakeRequest handshakeRequest) {
    this.handshakeRequest = handshakeRequest;
  }

  public HandshakeRequest getHandshakeRequest() {
    return handshakeRequest;
  }

  public void setHandshakeRequest(HandshakeRequest handshakeRequest) {
    this.handshakeRequest = handshakeRequest;
  }
}
