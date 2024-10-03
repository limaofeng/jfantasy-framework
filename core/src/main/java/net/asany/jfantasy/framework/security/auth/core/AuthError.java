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

import org.springframework.util.Assert;

public class AuthError {
  private final String errorCode;
  private final String description;
  private final String uri;

  public AuthError(String errorCode) {
    this(errorCode, null, null);
  }

  public AuthError(String errorCode, String description, String uri) {
    Assert.hasText(errorCode, "errorCode cannot be empty");
    this.errorCode = errorCode;
    this.description = description;
    this.uri = uri;
  }

  public final String getErrorCode() {
    return this.errorCode;
  }

  public final String getDescription() {
    return this.description;
  }

  public final String getUri() {
    return this.uri;
  }

  @Override
  public String toString() {
    return "["
        + this.getErrorCode()
        + "] "
        + ((this.getDescription() != null) ? this.getDescription() : "");
  }
}
