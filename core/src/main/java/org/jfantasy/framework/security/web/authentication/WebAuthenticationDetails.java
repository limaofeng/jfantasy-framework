package org.jfantasy.framework.security.web.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * WebAuthenticationDetails
 *
 * @author limaofeng
 */
public class WebAuthenticationDetails {

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
    if (obj instanceof WebAuthenticationDetails) {
      WebAuthenticationDetails other = (WebAuthenticationDetails) obj;
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

  public String getRemoteAddress() {
    return this.remoteAddress;
  }

  public String getSessionId() {
    return this.sessionId;
  }

  public HttpServletRequest getRequest() {
    return request;
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
