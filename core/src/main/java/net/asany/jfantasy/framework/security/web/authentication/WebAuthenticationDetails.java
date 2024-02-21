package net.asany.jfantasy.framework.security.web.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;

/**
 * WebAuthenticationDetails
 *
 * @author limaofeng
 */
@Getter
public class WebAuthenticationDetails implements AuthenticationDetails {

  private final String remoteAddress;
  private final String sessionId;

  private HttpServletRequest request;

  public WebAuthenticationDetails(HttpServletRequest request) {
    this.request = request;
    this.remoteAddress = request.getRemoteAddr();
    HttpSession session = request.getSession(false);
    this.sessionId = (session != null) ? session.getId() : null;
  }

  private WebAuthenticationDetails(final String remoteAddress, final String sessionId) {
    this.remoteAddress = remoteAddress;
    this.sessionId = sessionId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof WebAuthenticationDetails other) {
      if ((this.remoteAddress == null) && (other.getRemoteAddress() != null)) {
        return false;
      }
      if ((this.remoteAddress != null) && (other.getRemoteAddress() == null)) {
        return false;
      }
      if (this.remoteAddress != null) {
        if (!this.remoteAddress.equals(other.getRemoteAddress())) {
          return false;
        }
      }
      if ((this.sessionId == null) && (other.getSessionId() != null)) {
        return false;
      }
      if ((this.sessionId != null) && (other.getSessionId() == null)) {
        return false;
      }
      if (this.sessionId != null) {
        return this.sessionId.equals(other.getSessionId());
      }
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int code = 7654;
    if (this.remoteAddress != null) {
      code = code * (this.remoteAddress.hashCode() % 7);
    }
    if (this.sessionId != null) {
      code = code * (this.sessionId.hashCode() % 7);
    }
    return code;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + " ["
        + "RemoteIpAddress="
        + this.getRemoteAddress()
        + ", "
        + "SessionId="
        + this.getSessionId()
        + "]";
  }
}
