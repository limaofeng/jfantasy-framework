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
package net.asany.jfantasy.framework.security.auth.oauth2.server.web;

import java.util.List;
import net.asany.jfantasy.framework.security.auth.Token;
import net.asany.jfantasy.framework.security.auth.TokenResolver;

public class CompositeTokenResolver<T> {

  private final List<TokenResolver<T>> resolvers;

  @SafeVarargs
  public CompositeTokenResolver(TokenResolver<T>... resolvers) {
    this.resolvers = List.of(resolvers);
  }

  public Token resolveToken(T request) {
    for (TokenResolver<T> resolver : resolvers) {
      if (!resolver.supports(request)) {
        continue;
      }
      String token = resolver.resolve(request);
      if (token != null) {
        return new Token(resolver.getAuthType(), resolver.getAuthTokenType(), token);
      }
    }
    return null;
  }
}
