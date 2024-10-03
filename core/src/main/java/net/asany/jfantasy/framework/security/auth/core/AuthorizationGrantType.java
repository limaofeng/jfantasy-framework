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

import java.io.Serializable;
import org.springframework.util.Assert;

/**
 * 授予类型
 *
 * <p>授权类型是指客户端如何获得用户的授权。
 */
public record AuthorizationGrantType(String value) implements Serializable {

  public static final AuthorizationGrantType AUTHORIZATION_CODE =
      new AuthorizationGrantType("authorization_code");

  @Deprecated
  public static final AuthorizationGrantType IMPLICIT = new AuthorizationGrantType("implicit");

  public static final AuthorizationGrantType REFRESH_TOKEN =
      new AuthorizationGrantType("refresh_token");

  public static final AuthorizationGrantType CLIENT_CREDENTIALS =
      new AuthorizationGrantType("client_credentials");

  public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");

  /**
   * @since 5.5
   */
  public static final AuthorizationGrantType JWT_BEARER =
      new AuthorizationGrantType("urn:ietf:params:oauth:grant-type:jwt-bearer");

  public AuthorizationGrantType {
    Assert.hasText(value, "value cannot be empty");
  }

  public static AuthorizationGrantType valueOf(String value) {
    return switch (value) {
      case "authorization_code" -> AUTHORIZATION_CODE;
      case "refresh_token" -> REFRESH_TOKEN;
      case "client_credentials" -> CLIENT_CREDENTIALS;
      case "password" -> PASSWORD;
      default -> throw new IllegalStateException("Unexpected value: " + value);
    };
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    AuthorizationGrantType that = (AuthorizationGrantType) obj;
    return this.value().equals(that.value());
  }

  @Override
  public int hashCode() {
    return this.value().hashCode();
  }
}
