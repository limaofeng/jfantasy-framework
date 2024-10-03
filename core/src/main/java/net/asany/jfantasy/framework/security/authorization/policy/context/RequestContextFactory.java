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
package net.asany.jfantasy.framework.security.authorization.policy.context;

import java.util.List;
import net.asany.jfantasy.framework.security.authentication.Authentication;

public class RequestContextFactory {

  private final List<RequestContextBuilder> builders;

  public RequestContextFactory(List<RequestContextBuilder> builders) {
    this.builders = builders;
  }

  public RequestContext create(Authentication authentication) {
    RequestContextBuilder requestContextBuilder =
        this.builders.stream()
            .filter(builder -> builder.supports(authentication))
            .findFirst()
            .orElseThrow(
                () -> new RuntimeException("No RequestContextBuilder found for authentication"));
    return requestContextBuilder.build(authentication);
  }
}
