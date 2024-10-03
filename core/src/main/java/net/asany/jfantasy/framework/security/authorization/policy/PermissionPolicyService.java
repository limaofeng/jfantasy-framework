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
 * 权限策略服务
 *
 * <p>权限策略服务提供了权限策略的管理功能，包括创建、更新、删除、查询、列出所有权限策略等。
 */
public interface PermissionPolicyService {
  /**
   * 创建权限策略
   *
   * @param policy 权限策略
   * @return 创建后的权限策略
   */
  PermissionPolicy createPolicy(PermissionPolicy policy);

  /**
   * 更新权限策略
   *
   * @param policyId 权限策略ID
   * @param policy 权限策略
   * @return 更新后的权限策略
   */
  PermissionPolicy updatePolicy(String policyId, PermissionPolicy policy);

  /**
   * 删除权限策略
   *
   * @param policyId 权限策略ID
   */
  void deletePolicy(String policyId);

  /**
   * 获取权限策略
   *
   * @param policyId 权限策略ID
   * @return 权限策略
   */
  PermissionPolicy getPolicy(String policyId);

  /**
   * 列出所有权限策略
   *
   * @return 权限策略列表
   */
  List<PermissionPolicy> listAllPolicies();
}
