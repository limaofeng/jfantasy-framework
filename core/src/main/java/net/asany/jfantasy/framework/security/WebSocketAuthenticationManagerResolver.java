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
package net.asany.jfantasy.framework.security;

import jakarta.websocket.server.HandshakeRequest;
import net.asany.jfantasy.framework.security.authentication.AuthenticationManagerResolver;

/**
 * AuthenticationManagerResolver
 *
 * @author limaofeng
 */
public class WebSocketAuthenticationManagerResolver
    implements AuthenticationManagerResolver<HandshakeRequest> {

  private final AuthenticationManager authenticationManager;

  public WebSocketAuthenticationManagerResolver(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public AuthenticationManager resolve(HandshakeRequest context) {
    return this.authenticationManager;
  }
}
