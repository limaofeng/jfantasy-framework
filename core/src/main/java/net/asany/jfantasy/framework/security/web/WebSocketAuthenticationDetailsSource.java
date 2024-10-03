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
package net.asany.jfantasy.framework.security.web;

import jakarta.websocket.server.HandshakeRequest;
import net.asany.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import net.asany.jfantasy.framework.security.web.authentication.WebSocketAuthenticationDetails;

/**
 * WebSocketAuthenticationDetailsSource
 *
 * @author limaofeng
 */
public class WebSocketAuthenticationDetailsSource
    implements AuthenticationDetailsSource<HandshakeRequest, WebSocketAuthenticationDetails> {

  @Override
  public WebSocketAuthenticationDetails buildDetails(HandshakeRequest handshakeRequest) {
    return new WebSocketAuthenticationDetails(handshakeRequest);
  }
}
