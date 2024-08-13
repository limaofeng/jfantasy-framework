package net.asany.jfantasy.framework.security.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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

  default <A> Optional<A> getAttribute(String name) {
    Map<String, Object> attrs = getAttributes();
    if (attrs.containsKey(name)) {
      return Optional.of((A) attrs.get(name));
    }
    return Optional.empty();
  }

  default Map<String, Object> getAttributes() {
    return Collections.emptyMap();
  }

  default Collection<GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }
}
