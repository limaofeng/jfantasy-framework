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
}
