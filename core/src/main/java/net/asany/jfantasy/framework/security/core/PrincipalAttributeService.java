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
