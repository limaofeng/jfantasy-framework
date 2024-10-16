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
package net.asany.jfantasy.framework.security.core;

import java.security.Principal;
import java.util.*;

/**
 * 身份验证的主体
 *
 * @author limaofeng
 */
public interface AuthenticatedPrincipal extends Principal {

  Long getId();

  /**
   * 当事人名称
   *
   * @return String
   */
  String getName();

  default String getTenantId() {
    return (String) this.getAttribute("tenantId").orElse(null);
  }

  default <A> Optional<A> getAttribute(String name) {
    Map<String, Object> attrs = getAttributes();
    if (attrs.containsKey(name)) {
      //noinspection unchecked
      return Optional.of((A) attrs.get(name));
    }
    return Optional.empty();
  }

  default Map<String, Object> getAttributes() {
    return Collections.emptyMap();
  }

  default Set<GrantedAuthority> getAuthorities() {
    return Collections.emptySet();
  }

  default Set<GrantedAuthority> getAuthorities(boolean force) {
    return Collections.emptySet();
  }
}
