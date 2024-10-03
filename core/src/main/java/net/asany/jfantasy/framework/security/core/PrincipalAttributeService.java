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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.asany.jfantasy.framework.security.PrincipalDynamicAttribute;

/** 用户属性服务 管理并提供所有用户的动态属性 */
public class PrincipalAttributeService {

  // 存储每个属性名称对应的动态属性实现
  private final Map<String, PrincipalDynamicAttribute<AuthenticatedPrincipal, ?>>
      attributeRegistry = new HashMap<>();

  /**
   * 注册动态属性
   *
   * @param attribute 动态属性实现
   */
  public void registerAttribute(PrincipalDynamicAttribute<?, ?> attribute) {
    attributeRegistry.put(
        attribute.name(), (PrincipalDynamicAttribute<AuthenticatedPrincipal, ?>) attribute);
  }

  /**
   * 判断是否存在某个动态属性
   *
   * @param name 属性名称
   * @return 是否存在
   */
  public boolean hasAttribute(String name) {
    return attributeRegistry.containsKey(name);
  }

  /**
   * 获取用户的某个动态属性的值
   *
   * @param principal 用户对象
   * @param attributeName 属性名称
   * @return 属性值
   */
  public <V> Optional<V> getAttributeValue(AuthenticatedPrincipal principal, String attributeName) {
    PrincipalDynamicAttribute<AuthenticatedPrincipal, ?> attribute =
        attributeRegistry.get(attributeName);
    if (attribute != null) {
      return (Optional<V>) attribute.value(principal);
    }
    throw new IllegalArgumentException("Attribute not found: " + attributeName);
  }

  /**
   * 获取所有动态属性的值
   *
   * @param principal 用户对象
   * @return 动态属性值映射
   */
  public Map<String, Object> getAllAttributes(AuthenticatedPrincipal principal) {
    Map<String, Object> attributes = new HashMap<>();
    for (PrincipalDynamicAttribute<AuthenticatedPrincipal, ?> attribute :
        attributeRegistry.values()) {
      attributes.put(attribute.name(), attribute.value(principal));
    }
    return attributes;
  }
}
