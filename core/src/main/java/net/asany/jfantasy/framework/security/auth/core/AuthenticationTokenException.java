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

import lombok.Getter;
import net.asany.jfantasy.framework.security.AuthenticationException;
import org.springframework.util.Assert;

@Getter
public class AuthenticationTokenException extends AuthenticationException {

  private final AuthError error;

  public AuthenticationTokenException(String errorCode) {
    this(new AuthError(errorCode));
  }

  public AuthenticationTokenException(AuthError error) {
    this(error, error.getDescription());
  }

  public AuthenticationTokenException(AuthError error, Throwable cause) {
    this(error, cause.getMessage(), cause);
  }

  public AuthenticationTokenException(AuthError error, String message) {
    this(error, message, null);
  }

  public AuthenticationTokenException(AuthError error, String message, Throwable cause) {
    super(message, cause);
    Assert.notNull(error, "error cannot be null");
    this.error = error;
  }
}
