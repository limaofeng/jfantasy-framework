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

import java.time.Instant;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.TokenUsage;

public interface AuthenticationDetails {

  AuthType getAuthType();

  TokenUsage getTokenUsage();

  String getClientId();

  ClientDetails getClientDetails();

  void setClientDetails(ClientDetails clientDetails);

  ClientSecret getClientSecret();

  void setClientSecret(ClientSecret clientSecret);

  Instant getExpiresAt();

  void setExpiresAt(Instant expiresAt);

  default AuthenticationDetails update(AuthenticationDetails details) {
    throw new UnsupportedOperationException("update");
  }
}
