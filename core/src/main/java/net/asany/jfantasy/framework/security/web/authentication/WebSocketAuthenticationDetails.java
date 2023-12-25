package net.asany.jfantasy.framework.security.web.authentication;

import jakarta.websocket.server.HandshakeRequest;
import lombok.Getter;

/**
 * WebSocketAuthenticationDetails
 *
 * @author limaofeng
 */
@Getter
public class WebSocketAuthenticationDetails {

  private HandshakeRequest handshakeRequest;

  public WebSocketAuthenticationDetails(HandshakeRequest handshakeRequest) {
    this.handshakeRequest = handshakeRequest;
  }

  public void setHandshakeRequest(HandshakeRequest handshakeRequest) {
    this.handshakeRequest = handshakeRequest;
  }
}
