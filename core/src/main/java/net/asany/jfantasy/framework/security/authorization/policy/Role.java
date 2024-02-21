package net.asany.jfantasy.framework.security.authorization.policy;

import java.util.List;

/**
 * 角色
 *
 * <p>角色是权限的集合，一个用户可以拥有多个角色，角色可以继承其他角色的权限
 */
public interface Role {

  /**
   * 角色ID
   *
   * @return 角色ID
   */
  String getId();

  /**
   * 角色描述
   *
   * @return 角色描述
   */
  String getDescription();

  /**
   * 角色权限
   *
   * @return 角色权限
   */
  List<String> getPermissions();
}
