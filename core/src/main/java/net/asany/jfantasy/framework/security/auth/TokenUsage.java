package net.asany.jfantasy.framework.security.auth;

import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.core.ClientSecretType;

/** Token 用途 */
@Getter
public enum TokenUsage {
  OAUTH(ClientSecretType.OAUTH2),
  SESSION(ClientSecretType.ROTATING),
  PERSONAL_ACCESS_TOKEN(ClientSecretType.STATIC);

  private final ClientSecretType clientSecretType;

  TokenUsage(ClientSecretType clientSecretType) {
    this.clientSecretType = clientSecretType;
  }
}
