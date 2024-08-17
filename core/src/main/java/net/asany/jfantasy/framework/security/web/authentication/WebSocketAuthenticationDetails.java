package net.asany.jfantasy.framework.security.web.authentication;

import jakarta.websocket.server.HandshakeRequest;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.core.AbstractAuthenticationDetails;

/**
 * WebSocketAuthenticationDetails
 *
 * @author limaofeng
 */
@Getter
public class WebSocketAuthenticationDetails extends AbstractAuthenticationDetails {

  private final HandshakeRequest handshakeRequest;

  public WebSocketAuthenticationDetails(HandshakeRequest handshakeRequest) {
    this.handshakeRequest = handshakeRequest;
  }
}
