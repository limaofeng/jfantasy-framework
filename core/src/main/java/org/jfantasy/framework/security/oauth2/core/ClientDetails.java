package org.jfantasy.framework.security.oauth2.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 客户端信息
 *
 * @author limaofeng
 */
public interface ClientDetails {

  /**
   * 附加信息
   *
   * @return Map<String, Object>
   */
  Map<String, Object> getAdditionalInformation();

  /**
   * 权限
   *
   * @return Collection<GrantedAuthority>
   */
  Collection<GrantedAuthority> getAuthorities();

  /**
   * 支持的授权方式
   *
   * @return Set<String>
   */
  Set<String> getAuthorizedGrantTypes();

  /**
   * 客户端 ID
   *
   * @return String
   */
  String getClientId();

  /**
   * 客户端密钥
   *
   * @return String
   */
  String getClientSecret();

  /**
   * 客户端密钥（多个）
   *
   * @return Set<String>
   */
  default Set<String> getClientSecrets() {
    Set<String> secrets = new HashSet<>();
    secrets.add(this.getClientSecret());
    return secrets;
  }

  /**
   * 跳转地址
   *
   * @return String
   */
  String getRedirectUri();

  /**
   * 授权范围
   *
   * @return Set<String>
   */
  Set<String> getScope();

  /**
   * Token 失效时间（分钟）
   *
   * @return int
   */
  int getTokenExpires();
}
