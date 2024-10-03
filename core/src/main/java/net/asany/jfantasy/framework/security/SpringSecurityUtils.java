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

import net.asany.jfantasy.framework.security.authentication.Authentication;

/**
 * SpringSecurityUtils
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 13:27
 */
public class SpringSecurityUtils {

  private SpringSecurityUtils() {}

  public SecurityContext getContext() {
    return SecurityContextHolder.getContext();
  }

  public static <T extends Authentication> T getAuthentication() {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context == null || context.getAuthentication() == null) {
      return null;
    }
    return (T) context.getAuthentication();
  }

  public static <T extends LoginUser> T getCurrentUser(Class<T> clazz) {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context == null || !context.isAuthenticated()) {
      return null;
    }
    return context.getPrincipal(clazz);
  }

  public static LoginUser getCurrentUser() {
    return getCurrentUser(LoginUser.class);
  }
}
