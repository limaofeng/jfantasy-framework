package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.AuthenticationException;

/**
 * 身份验证程序
 *
 * @param <T>
 * @author limaofeng
 */
public interface AuthenticationProvider<T extends Authentication> {

  /**
   * 判断是否支持此身份认证逻辑
   *
   * @param authentication 身份验证
   * @return Boolean
   */
  boolean supports(Class<? extends Authentication> authentication);

  /**
   * 进行身份验证
   *
   * @param authentication 身份验证
   * @return Boolean
   * @throws AuthenticationException 异常
   */
  Authentication authenticate(T authentication) throws AuthenticationException;
}
