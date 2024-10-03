/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
