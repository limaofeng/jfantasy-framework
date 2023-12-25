package net.asany.jfantasy.framework.security.core.userdetails;

/**
 * 基于 Token 获取用户信息
 *
 * @author limaofeng
 */
public interface SimpleUserDetailsService<T> {

  /**
   * 获取用户
   *
   * @param token 凭证
   * @return 返回用户
   * @throws UsernameNotFoundException 未查找用户
   */
  UserDetails loadUserByToken(T token) throws UsernameNotFoundException;
}
