package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationManager;

/**
 * Authentication Manager Resolver
 *
 * @author limaofeng
 */
public interface AuthenticationManagerResolver<C> {

  /**
   * 查询 AuthenticationManager
   *
   * @param context 上下文
   * @return AuthenticationManager
   */
  AuthenticationManager resolve(C context);
}
