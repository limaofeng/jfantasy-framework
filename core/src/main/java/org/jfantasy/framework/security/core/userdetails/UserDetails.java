package org.jfantasy.framework.security.core.userdetails;

import java.io.Serializable;
import java.util.Collection;
import org.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 用户详情
 *
 * @author limaofeng
 */
public interface UserDetails extends Serializable {

  /**
   * 密码
   *
   * @return 密码
   */
  String getPassword();

  /**
   * 用户名
   *
   * @return 用户名
   */
  String getUsername();

  /**
   * 未过期
   *
   * @return 未过期
   */
  boolean isAccountNonExpired();

  /**
   * 未锁定
   *
   * @return 未锁定
   */
  boolean isAccountNonLocked();

  /**
   * 凭证过期
   *
   * @return 凭证过期
   */
  boolean isCredentialsNonExpired();

  /**
   * 启用
   *
   * @return 启用
   */
  boolean isEnabled();

  /**
   * 授予权限
   *
   * @return 授予权限
   */
  Collection<? extends GrantedAuthority> getAuthorities();
}
