package net.asany.jfantasy.framework.security.authorization;

import net.asany.jfantasy.framework.security.authentication.Authentication;

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
  boolean authorize(String resource, String action, Authentication authentication);
}
