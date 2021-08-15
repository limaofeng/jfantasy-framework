package org.jfantasy.framework.security.core.userdetails;

/**
 * 用户服务
 *
 * @author limaofeng
 */
public interface UserDetailsService {

  /**
   * 通过用户名查询用户
   *
   * @param username 用户名
   * @return 用户
   * @throws UsernameNotFoundException 未查询到用户
   */
  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
