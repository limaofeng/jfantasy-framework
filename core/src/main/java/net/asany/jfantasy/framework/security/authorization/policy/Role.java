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
package net.asany.jfantasy.framework.security.authorization.policy;

import java.util.List;

/**
 * 角色
 *
 * <p>角色是权限的集合，一个用户可以拥有多个角色，角色可以继承其他角色的权限
 */
public interface Role {

  /**
   * 角色ID
   *
   * @return 角色ID
   */
  String getId();

  /**
   * 角色描述
   *
   * @return 角色描述
   */
  String getDescription();

  /**
   * 角色权限
   *
   * @return 角色权限
   */
  List<String> getPermissions();
}
