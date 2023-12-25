package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.AuthenticationException;

/**
 * 验证事件发布者
 *
 * @author limaofeng
 */
public interface AuthenticationEventPublisher {

  /**
   * 认证成功
   *
   * @param authentication 认证信息
   */
  void publishAuthenticationSuccess(Authentication authentication);

  /**
   * 认证失败
   *
   * @param exception 异常信息
   * @param authentication 认证信息
   */
  void publishAuthenticationFailure(
      AuthenticationException exception, Authentication authentication);
}
