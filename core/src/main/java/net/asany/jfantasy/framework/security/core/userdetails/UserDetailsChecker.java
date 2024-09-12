package net.asany.jfantasy.framework.security.core.userdetails;

import net.asany.jfantasy.framework.security.auth.AuthenticationToken;

/**
 * 用户详情检测器
 *
 * @author limaofeng
 */
public interface UserDetailsChecker {

  /**
   * 判断是否需要进行认证检查
   *
   * @param authenticationToken 认证令牌
   * @return 是否需要进行认证检查
   */
  default boolean needsCheck(AuthenticationToken<?> authenticationToken) {
    return true;
  }

  /**
   * 检查方法
   *
   * @param user 用户
   * @param authenticationToken 认证令牌
   */
  void check(UserDetails user, AuthenticationToken<?> authenticationToken);
}
