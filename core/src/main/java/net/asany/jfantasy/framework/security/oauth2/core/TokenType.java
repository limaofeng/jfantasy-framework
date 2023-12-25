package net.asany.jfantasy.framework.security.oauth2.core;

public enum TokenType {

  /** 个人 Token 不能续期，但可以设置有效期的 TOKEN */
  PERSONAL(ClientSecretType.PERSONAL_ACCESS_TOKEN),
  /** 标准 OAUTH 的认证 */
  TOKEN(ClientSecretType.OAUTH),
  /** SESSION 形式的授权 */
  SESSION(ClientSecretType.SESSION);

  private final ClientSecretType clientSecretType;

  TokenType(ClientSecretType clientSecretType) {
    this.clientSecretType = clientSecretType;
  }

  public ClientSecretType getClientSecretType() {
    return this.clientSecretType;
  }
}
