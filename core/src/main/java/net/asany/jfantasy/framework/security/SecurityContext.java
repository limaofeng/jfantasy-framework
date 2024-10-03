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

import lombok.Data;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;

/**
 * 安全上下文
 *
 * @author limaofeng
 */
@Data
public class SecurityContext {

  private Authentication authentication;

  public boolean isAuthenticated() {
    return this.authentication != null && this.authentication.isAuthenticated();
  }

  public <T extends UserDetails> T getPrincipal(Class<T> clazz) {
    if (!isAuthenticated()) {
      return null;
    }
    Object principal = this.authentication.getPrincipal();
    if (principal instanceof UserDetails) {
      return clazz.cast(principal);
    }
    return null;
  }

  public boolean isUserInRole(String role) {
    UserDetails userDetails = getPrincipal(UserDetails.class);
    if (userDetails.getAuthorities() == null) {
      return false;
    }
    return userDetails.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
  }
}
