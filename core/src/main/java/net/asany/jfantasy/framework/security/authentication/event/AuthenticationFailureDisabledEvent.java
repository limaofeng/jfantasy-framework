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
package net.asany.jfantasy.framework.security.authentication.event;

import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationFailureDisabledEvent extends AbstractAuthenticationFailureEvent {

  public AuthenticationFailureDisabledEvent(
      Authentication authentication, AuthenticationException exception) {
    super(authentication, exception);
  }
}
