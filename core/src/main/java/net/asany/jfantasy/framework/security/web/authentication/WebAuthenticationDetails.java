/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.security.web.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.security.auth.core.AbstractAuthenticationDetails;

/**
 * WebAuthenticationDetails
 *
 * @author limaofeng
 */
@Getter
@NoArgsConstructor
public class WebAuthenticationDetails extends AbstractAuthenticationDetails {

  private String remoteAddress;
  private String sessionId;

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
