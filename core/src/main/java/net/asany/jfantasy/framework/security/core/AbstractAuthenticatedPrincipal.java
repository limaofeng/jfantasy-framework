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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.*;
import lombok.Setter;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.util.common.ObjectUtil;

public abstract class AbstractAuthenticatedPrincipal implements AuthenticatedPrincipal {

  /** 扩展属性 */
  @JsonIgnore private Map<String, Object> data;

  @Setter
  @JsonSerialize(using = GrantedAuthority.GrantedAuthoritiesSerializer.class)
  @JsonDeserialize(using = GrantedAuthority.GrantedAuthoritiesDeserializer.class)
  private Set<GrantedAuthority> authorities;

  @JsonAnySetter
  public void setAttribute(String key, Object value) {
    if (this.data == null) {
      this.data = new HashMap<>(0);
    }
    this.data.put(key, value);
  }

  @Override
  public <A> Optional<A> getAttribute(String name) {
    PrincipalAttributeServiceRegistry attributeServiceRegistry =
        SpringBeanUtils.getBeanByType(PrincipalAttributeServiceRegistry.class);
    PrincipalAttributeService attributeService = attributeServiceRegistry.getService(getClass());
    if (attributeService.hasAttribute(name)) {
      return attributeService.getAttributeValue(this, name);
    }
    if (this.data == null) {
      this.data = new HashMap<>();
    }
    if (this.data.containsKey(name)) {
      //noinspection unchecked
      return Optional.of((A) this.data.get(name));
    }
    return Optional.empty();
  }

  @Override
  public Set<GrantedAuthority> getAuthorities() {
    return getAuthorities(false);
  }

  @Override
  public Set<GrantedAuthority> getAuthorities(boolean force) {
    // 如果已经获取过权限，不再重复获取
    if (!force && this.authorities != null) {
      return this.authorities;
    }
    // 获取所有的权限提供者
    String[] authorityProviders = SpringBeanUtils.getBeanNamesForType(AuthorityProvider.class);
    Set<GrantedAuthority> authorities = new HashSet<>();
    for (String provider : authorityProviders) {
      //noinspection unchecked
      AuthorityProvider<AuthenticatedPrincipal> authorityProvider =
          SpringBeanUtils.getBean(provider, AuthorityProvider.class);
      if (authorityProvider == null || !authorityProvider.supports(getClass())) {
        continue;
      }
      authorities.addAll(authorityProvider.getAuthorities(this));
    }
    return this.authorities = authorities;
  }

  @Override
  @JsonAnyGetter
  public Map<String, Object> getAttributes() {
    return ObjectUtil.defaultValue(this.data, Collections.emptyMap());
  }
}
