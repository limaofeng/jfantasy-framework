package org.jfantasy.framework.security.authentication;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import org.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 身份验证
 *
 * @author limaofeng
 */
public interface Authentication extends Principal, Serializable {

  /**
   * 权限
   *
   * @return Collection<GrantedAuthority>
   */
  Collection<GrantedAuthority> getAuthorities();

  /**
   * 凭证 密码 / Token 等
   *
   * @return 凭证
   */
  Object getCredentials();

  /**
   * 详情
   *
   * @return 认证详情
   */
  Object getDetails();

  /**
   * 当事人
   *
   * @return 用户
   */
  Object getPrincipal();

  /**
   * 是否授权 （登录）
   *
   * @return boolean
   */
  boolean isAuthenticated();

  /**
   * 设置
   *
   * @param isAuthenticated boolean
   */
  void setAuthenticated(boolean isAuthenticated);
}
