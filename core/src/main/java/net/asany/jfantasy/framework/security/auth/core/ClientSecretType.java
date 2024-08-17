package net.asany.jfantasy.framework.security.auth.core;

public enum ClientSecretType {
  STATIC, // 静态密钥
  ROTATING, // 旋转密钥
  EPHEMERAL, // 临时密钥
  OAUTH2, // OAuth2 密钥
  JWT_SIGNING, // JWT 签名密钥
  API_KEY, // API 密钥
  HMAC, // HMAC 密钥
  CLIENT_CERTIFICATE, // 客户端证书
  ANONYMOUS, // 匿名密钥
  SYMMETRIC_KEY; // 对称密钥

  public boolean isAutoRenewable() {
    return this == ROTATING;
  }

  public boolean supportsRefreshToken() {
    return OAUTH2 == this;
  }
}
