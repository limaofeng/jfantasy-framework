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

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

public interface AuthToken {

  String getClientId();

  String getTokenValue();

  Instant getIssuedAt();

  Instant getExpiresAt();

  Set<String> getScopes();

  default boolean isExpired() {
    return getExpiresAt() != null && Instant.now().isAfter(getExpiresAt());
  }

  default Long getExpiresIn() {
    if (getExpiresAt() == null) {
      return null;
    }
    return Duration.between(getIssuedAt(), getExpiresAt()).getSeconds();
  }
}
