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
package net.asany.jfantasy.framework.security.core;

import java.util.Set;

/** 权限提供者接口，用于获取指定实体的权限列表。 */
public interface AuthorityProvider<T extends AuthenticatedPrincipal> {

  /**
   * 是否支持指定的权限来源实体类型。
   *
   * @param principalType 权限来源实体类型
   * @return 是否支持
   */
  boolean supports(Class<?> principalType);

  /**
   * 获取权限列表，可以从当前用户或指定的实体中获取。
   *
   * @param principal 可选的权限来源实体（如用户、角色、组等）
   * @return 权限列表
   */
  Set<GrantedAuthority> getAuthorities(T principal);
}
