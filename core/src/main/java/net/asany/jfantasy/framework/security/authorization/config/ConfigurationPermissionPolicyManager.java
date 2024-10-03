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
package net.asany.jfantasy.framework.security.authorization.config;

import java.util.List;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicy;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicyManager;

public class ConfigurationPermissionPolicyManager implements PermissionPolicyManager {

  private final AuthorizationConfiguration configuration;

  public ConfigurationPermissionPolicyManager(AuthorizationConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void assignPolicyToUser(String userId, String policyId) {}

  @Override
  public void removePolicyFromUser(String userId, String policyId) {}

  @Override
  public List<PermissionPolicy> getPoliciesForUser(String userId) {
    return null;
  }

  @Override
  public List<PermissionPolicy> loadPolicies(Object credentials) {
    return List.of();
  }
}
