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
package net.asany.jfantasy.framework.security.authorization;

import java.util.Set;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;

/**
 * 基于策略的授权提供者
 *
 * <p>用于提供基于策略的授权
 */
public interface PolicyBasedAuthorizationProvider {

  /**
   * 授权
   *
   * @param resource 资源
   * @param action 操作
   * @param authentication 认证信息
   * @return 是否授权
   */
  default boolean authorize(String resource, String action, Authentication authentication) {
    return authorize(Set.of(resource), action, authentication);
  }

  /**
   * 授权
   *
   * @param resources 资源
   * @param action 操作
   * @param authentication 认证信息
   * @return 是否授权
   */
  boolean authorize(Set<String> resources, String action, Authentication authentication);

  /***
   * 获取操作对应的资源
   * @param operation      操作
   * @return 资源
   */
  ResourceAction getResourceActionForOperation(String operation);
}
