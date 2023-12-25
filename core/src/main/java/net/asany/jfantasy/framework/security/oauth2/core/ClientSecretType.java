package net.asany.jfantasy.framework.security.oauth2.core;

public enum ClientSecretType {
  /** 内部使用，用于模拟SESSION */
  SESSION,
  /** 标准的 OAUTH 认证 */
  OAUTH,
  /** 个人访问令牌 */
  PERSONAL_ACCESS_TOKEN
}
