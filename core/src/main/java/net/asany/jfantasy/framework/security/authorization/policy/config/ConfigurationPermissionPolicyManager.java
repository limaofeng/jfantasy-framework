package net.asany.jfantasy.framework.security.authorization.policy.config;

import java.util.List;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicy;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicyManager;

public class ConfigurationPermissionPolicyManager implements PermissionPolicyManager {

  private final Configuration configuration;

  public ConfigurationPermissionPolicyManager(Configuration configuration) {
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
