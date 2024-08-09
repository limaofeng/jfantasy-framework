package net.asany.jfantasy.framework.security.core;

import java.util.HashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.UserDynamicAttribute;

/** 用户属性服务 管理并提供所有用户的动态属性 */
public class UserAttributeService {

  // 存储每个属性名称对应的动态属性实现
  private final Map<String, UserDynamicAttribute<?>> attributeRegistry = new HashMap<>();

  /**
   * 注册动态属性
   *
   * @param attribute 动态属性实现
   */
  public void registerAttribute(UserDynamicAttribute<?> attribute) {
    attributeRegistry.put(attribute.name(), attribute);
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
   * @param user 用户对象
   * @param attributeName 属性名称
   * @return 属性值
   */
  public Object getAttributeValue(LoginUser user, String attributeName) {
    UserDynamicAttribute<?> attribute = attributeRegistry.get(attributeName);
    if (attribute != null) {
      return attribute.value(user);
    }
    throw new IllegalArgumentException("Attribute not found: " + attributeName);
  }

  /**
   * 获取所有动态属性的值
   *
   * @param user 用户对象
   * @return 动态属性值映射
   */
  public Map<String, Object> getAllAttributes(LoginUser user) {
    Map<String, Object> attributes = new HashMap<>();
    for (UserDynamicAttribute<?> attribute : attributeRegistry.values()) {
      attributes.put(attribute.name(), attribute.value(user));
    }
    return attributes;
  }
}
