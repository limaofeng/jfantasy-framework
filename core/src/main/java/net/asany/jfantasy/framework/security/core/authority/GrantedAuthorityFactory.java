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
package net.asany.jfantasy.framework.security.core.authority;

import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

public class GrantedAuthorityFactory {
  private final Map<String, AuthorityBuilder> builderMap = new HashMap<>();
  @Setter private AuthorityBuilder defaultAuthorityBuilder;

  public void registerBuilder(String prefix, AuthorityBuilder builder) {
    builderMap.put(prefix, builder);
  }

  public GrantedAuthority createAuthority(String authorityString) {
    for (Map.Entry<String, AuthorityBuilder> entry : builderMap.entrySet()) {
      if (authorityString.startsWith(entry.getKey())) {
        return entry.getValue().buildAuthority(authorityString);
      }
    }
    if (defaultAuthorityBuilder != null) {
      return defaultAuthorityBuilder.buildAuthority(authorityString);
    }
    throw new IllegalArgumentException("No builder found for authority: " + authorityString);
  }
}
