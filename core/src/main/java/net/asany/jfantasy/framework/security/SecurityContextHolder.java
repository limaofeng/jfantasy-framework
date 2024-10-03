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

/**
 * 安全上下文持有人
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 13:27
 */
public class SecurityContextHolder {

  private static final ThreadLocal<SecurityContext> HOLDER = new ThreadLocal<>();

  public static SecurityContext getContext() {
    return HOLDER.get();
  }

  public static void setContext(SecurityContext context) {
    clear();
    HOLDER.set(context);
  }

  public static void clear() {
    SecurityContext securityContextHolder = HOLDER.get();
    if (securityContextHolder != null) {
      HOLDER.remove();
    }
  }

  public static SecurityContext createEmptyContext() {
    return new SecurityContext();
  }
}
