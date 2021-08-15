package org.jfantasy.framework.security.oauth2.core;

import java.util.Collection;
import java.util.Map;
import org.jfantasy.framework.security.core.AuthenticatedPrincipal;
import org.jfantasy.framework.security.core.GrantedAuthority;

public interface OAuth2AuthenticatedPrincipal extends AuthenticatedPrincipal {

  default <A> A getAttribute(String name) {
    return (A) getAttributes().get(name);
  }

  Map<String, Object> getAttributes();

  Collection<? extends GrantedAuthority> getAuthorities();
}
