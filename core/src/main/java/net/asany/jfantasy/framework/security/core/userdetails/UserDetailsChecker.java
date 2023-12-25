package net.asany.jfantasy.framework.security.core.userdetails;

/**
 * @author limaofeng
 */
public interface UserDetailsChecker {

  /**
   * 检查方法
   *
   * @param user
   */
  void check(UserDetails user);
}
