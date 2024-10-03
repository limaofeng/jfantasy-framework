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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.TokenUsage;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractAuthenticationDetails implements AuthenticationDetails {
  protected AuthType authType;
  protected TokenUsage tokenUsage;

  @JsonProperty("client_id")
  protected String clientId;

  @JsonIgnore protected ClientDetails clientDetails;

  @JsonProperty("client_secret")
  protected ClientSecret clientSecret;

  @JsonProperty("expires_at")
  protected Instant expiresAt;

  public AuthenticationDetails update(AuthenticationDetails details) {
    this.authType = details.getAuthType();
    this.tokenUsage = details.getTokenUsage();
    this.clientId = details.getClientId();
    this.clientDetails = details.getClientDetails();
    this.clientSecret = details.getClientSecret();
    this.expiresAt = details.getExpiresAt();
    return this;
  }
}
