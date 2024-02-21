package net.asany.jfantasy.framework.security.auth.core;

import java.util.*;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 客户端信息
 *
 * @author limaofeng
 */
public interface ClientDetails {

  /**
   * 附加信息
   *
   * @return Map
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
   * 客户端密钥（多个）
   *
   * @param type 密钥类型
   * @return Set<String>
   */
  Set<String> getClientSecrets(ClientSecretType type);

  /**
   * 客户端密钥
   *
   * @return String
   */
  default String getClientSecret(ClientSecretType type) {
    Set<String> secrets = this.getClientSecrets(type);
    return secrets.stream().findFirst().orElse(null);
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
   * @return Integer
   */
  Integer getTokenExpires(TokenType tokenType);
}
