package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationException;

/**
 * 验证事件发布者
 *
 * @author limaofeng
 */
public interface AuthenticationEventPublisher {

  /**
   * 认证成功
   *
   * @param authentication
   */
  void publishAuthenticationSuccess(Authentication authentication);

  /**
   * 认证失败
   *
   * @param exception
   * @param authentication
   */
  void publishAuthenticationFailure(
      AuthenticationException exception, Authentication authentication);
}
