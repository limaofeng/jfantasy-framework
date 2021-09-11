package org.jfantasy.framework.security;

import lombok.Data;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.core.userdetails.UserDetails;

/**
 * 安全上下文
 *
 * @author limaofeng
 */
@Data
public class SecurityContext {

  private Authentication authentication;

  public boolean isAuthenticated() {
    return this.authentication != null && this.authentication.isAuthenticated();
  }

  public <T extends UserDetails> T getPrincipal(Class<T> clazz) {
    if (!isAuthenticated()) {
      return null;
    }
    Object principal = this.authentication.getPrincipal();
    if (principal instanceof UserDetails) {
      return clazz.cast(principal);
    }
    return null;
  }

  public boolean isUserInRole(String role) {
    UserDetails userDetails = getPrincipal(UserDetails.class);
    if (userDetails.getAuthorities() == null) {
      return false;
    }
    return userDetails.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
  }
}
