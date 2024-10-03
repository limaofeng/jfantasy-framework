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

import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;

@Data
@Builder
public class DefaultUserDetails implements UserDetails {
  private String username;
  private String password;
  private Collection<GrantedAuthority> authorities;
  @Builder.Default private boolean enabled = true;
  @Builder.Default private boolean accountNonExpired = true;
  @Builder.Default private boolean accountNonLocked = true;
  @Builder.Default private boolean credentialsNonExpired = true;
}
