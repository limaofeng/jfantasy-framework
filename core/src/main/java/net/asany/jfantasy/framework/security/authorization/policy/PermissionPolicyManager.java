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

public interface PermissionPolicyManager {
  /**
   * 为用户分配权限策略
   *
   * @param userId 用户ID
   * @param policyId 权限策略ID
   */
  void assignPolicyToUser(String userId, String policyId);

  /**
   * 删除用户的权限策略
   *
   * @param userId 用户ID
   * @param policyId 权限策略ID
   */
  void removePolicyFromUser(String userId, String policyId);

  /**
   * 获取用户的权限策略
   *
   * @param userId 用户ID
   * @return 权限策略
   */
  List<PermissionPolicy> getPoliciesForUser(String userId);

  /**
   * 加载权限策略
   *
   * @param credentials 凭证
   * @return 权限策略
   */
  List<PermissionPolicy> loadPolicies(Object credentials);
}
