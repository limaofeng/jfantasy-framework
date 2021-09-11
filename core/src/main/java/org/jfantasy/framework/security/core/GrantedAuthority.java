package org.jfantasy.framework.security.core;

/**
 * 授予的权限
 *
 * @author limaofeng
 */
public interface GrantedAuthority {
  /**
   * 权限编码
   *
   * @return Authority
   */
  String getAuthority();
}
