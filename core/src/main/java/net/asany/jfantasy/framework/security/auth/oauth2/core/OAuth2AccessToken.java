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
package net.asany.jfantasy.framework.security.auth.oauth2.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.core.AbstractAuthToken;
import net.asany.jfantasy.framework.security.auth.core.AccessToken;

@Getter
public class OAuth2AccessToken extends AbstractAuthToken implements AccessToken {

  public OAuth2AccessToken(
      String clientId, String tokenValue, Instant issuedAt, Instant expiresAt) {
    this(clientId, tokenValue, issuedAt, expiresAt, Collections.emptySet());
  }

  public OAuth2AccessToken(
      String clientId, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
    super(clientId, tokenValue, issuedAt, expiresAt, scopes);
  }

  @JsonCreator
  public OAuth2AccessToken(
      @JsonProperty("client_id") String clientId,
      @JsonProperty("token_value") String tokenValue,
      @JsonProperty("refresh_token_value") String refreshTokenValue,
      @JsonProperty("issued_at") Instant issuedAt,
      @JsonProperty("expires_at") Instant expiresAt,
      @JsonProperty("token") Set<String> scopes) {
    super(clientId, tokenValue, refreshTokenValue, issuedAt, expiresAt, scopes);
  }
}
