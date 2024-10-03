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
package net.asany.jfantasy.framework.security.auth.oauth2.server;

import org.springframework.http.HttpStatus;

public class BearerTokenErrors {
  private static final BearerTokenError DEFAULT_INVALID_REQUEST = invalidRequest("Invalid request");

  private static final BearerTokenError DEFAULT_INVALID_TOKEN = invalidToken("Invalid token");

  private static final BearerTokenError DEFAULT_INSUFFICIENT_SCOPE =
      insufficientScope("Insufficient scope", null);

  private static final String DEFAULT_URI = "https://tools.ietf.org/html/rfc6750#section-3.1";

  private BearerTokenErrors() {}

  public static BearerTokenError invalidRequest(String message) {
    try {
      return new BearerTokenError(
          BearerTokenErrorCodes.INVALID_REQUEST, HttpStatus.BAD_REQUEST, message, DEFAULT_URI);
    } catch (IllegalArgumentException ex) {
      return DEFAULT_INVALID_REQUEST;
    }
  }

  public static BearerTokenError invalidToken(String message) {
    try {
      return new BearerTokenError(
          BearerTokenErrorCodes.INVALID_TOKEN, HttpStatus.UNAUTHORIZED, message, DEFAULT_URI);
    } catch (IllegalArgumentException ex) {
      return DEFAULT_INVALID_TOKEN;
    }
  }

  public static BearerTokenError insufficientScope(String message, String scope) {
    try {
      return new BearerTokenError(
          BearerTokenErrorCodes.INSUFFICIENT_SCOPE,
          HttpStatus.FORBIDDEN,
          message,
          DEFAULT_URI,
          scope);
    } catch (IllegalArgumentException ex) {
      return DEFAULT_INSUFFICIENT_SCOPE;
    }
  }
}
