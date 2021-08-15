package org.jfantasy.framework.security.authentication;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import org.jfantasy.framework.security.core.GrantedAuthority;

public interface Authentication extends Principal, Serializable {

  Collection<GrantedAuthority> getAuthorities();

  /**
   * 凭证 密码 / Token 等
   *
   * @return
   */
  Object getCredentials();

  Object getDetails();

  Object getPrincipal();

  boolean isAuthenticated();

  void setAuthenticated(boolean isAuthenticated);
}
