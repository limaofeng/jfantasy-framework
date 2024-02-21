package net.asany.jfantasy.framework.security.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 身份验证的主体
 *
 * @author limaofeng
 */
public interface AuthenticatedPrincipal {

  /**
   * 当事人名称
   *
   * @return String
   */
  String getName();

  default <A> A getAttribute(String name) {
    //noinspection unchecked
    return (A) getAttributes().get(name);
  }

  default Map<String, Object> getAttributes() {
    return Collections.emptyMap();
  }

  default Collection<GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }
}
