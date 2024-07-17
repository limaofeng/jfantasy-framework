package net.asany.jfantasy.framework.security.authorization;

import java.util.Set;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;

/**
 * 基于策略的授权提供者
 *
 * <p>用于提供基于策略的授权
 */
public interface PolicyBasedAuthorizationProvider {

  /**
   * 授权
   *
   * @param resource 资源
   * @param action 操作
   * @param authentication 认证信息
   * @return 是否授权
   */
  default boolean authorize(String resource, String action, Authentication authentication) {
    return authorize(Set.of(resource), action, authentication);
  }

  /**
   * 授权
   *
   * @param resources 资源
   * @param action 操作
   * @param authentication 认证信息
   * @return 是否授权
   */
  boolean authorize(Set<String> resources, String action, Authentication authentication);

  /***
   * 获取操作对应的资源
   * @param operation      操作
   * @return 资源
   */
  ResourceAction getResourceActionForOperation(String operation);
}
