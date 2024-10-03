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
package net.asany.jfantasy.framework.security.auth.base;

import java.util.Collections;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

@Getter
public class AnonymousAuthenticationToken extends AbstractAuthenticationToken<String> {

  private final AuthType authType;

  private final String credentials;

  public AnonymousAuthenticationToken(String token) {
    super(Collections.emptyList());
    Assert.hasText(token, "token cannot be empty");
    this.authType = AuthType.BASIC;
    this.credentials = token;
  }

  public AnonymousAuthenticationToken(String token, AuthenticationDetails details) {
    this(token);
    this.details = details;
  }

  @Override
  public String getCredentials() {
    return this.credentials;
  }

  @Override
  public String getPrincipal() {
    return this.credentials;
  }
}
