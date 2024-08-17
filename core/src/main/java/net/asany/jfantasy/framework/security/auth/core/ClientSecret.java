package net.asany.jfantasy.framework.security.auth.core;

public interface ClientSecret {

  String getId();

  String getSecretValue();

  /**
   * 过期时长 (单位分钟)
   *
   * @return 过期时长
   */
  Integer getTokenExpires();

  /**
   * 客户端密钥类型
   *
   * @return 密钥类型
   */
  ClientSecretType getType();
}
