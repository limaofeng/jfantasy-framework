package net.asany.jfantasy.framework.security.authorization.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authorization.PolicyBasedAuthorizationProvider;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicy;
import net.asany.jfantasy.framework.security.authorization.policy.PolicyEffect;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContext;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.PolicyAuthority;
import net.asany.jfantasy.framework.security.core.authority.RoleAuthority;

@Slf4j
public class ConfigurationPolicyBasedAuthorizationProvider
    implements PolicyBasedAuthorizationProvider {

  private final AuthorizationConfiguration configuration;
  private final RequestContextFactory requestContextFactory;

  public ConfigurationPolicyBasedAuthorizationProvider(
      RequestContextFactory requestContextFactory, AuthorizationConfiguration configuration) {
    this.configuration = configuration;
    this.requestContextFactory = requestContextFactory;
  }

  @Override
  public boolean authorize(Set<String> resources, String action, Authentication authentication) {
    if (!authentication.isAuthenticated()) {
      return configuration.getDefaultPolicy().getEffect() == PolicyEffect.ALLOW;
    }

    RequestContext requestContext = requestContextFactory.create(authentication);

    Collection<GrantedAuthority> authorities = authentication.getAuthorities();

    boolean isAuthorized = hasPermission(resources, action, requestContext, authorities);

    // 只有在没有匹配的规则时，才考虑默认策略
    if (requestContext.getMatchedRules().isEmpty()) {
      log.warn("No rules matched for resource: {}, action: {}", resources, action);
      return configuration.getDefaultPolicy().getEffect().isAllow();
    }

    log.info("Matched rules: {}", requestContext.getMatchedRules());
    return isAuthorized;
  }

  @Override
  public ResourceAction getResourceActionForOperation(String operation) {
    return configuration.getResourceActionForOperation(operation);
  }

  private boolean hasPermission(
      Set<String> resources,
      String action,
      RequestContext requestContext,
      Collection<GrantedAuthority> authorities) {
    // 逐个检查权限
    for (GrantedAuthority authority : authorities) {
      // 获取权限策略
      List<PermissionPolicy> policies = getPoliciesForAuthority(authority);
      // 逐个检查权限策略
      for (PermissionPolicy policy : policies) {
        // 逐个检查资源
        for (String resource : resources) {
          // 如果有访问权限，返回true
          if (policy.hasPermission(resource, action, requestContext)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private List<PermissionPolicy> getPoliciesForAuthority(GrantedAuthority authority) {
    if (authority instanceof RoleAuthority roleAuthority) {
      return configuration.getPolicyForRole(roleAuthority.getRole());
    } else if (authority instanceof PolicyAuthority policyAuthority) {
      return configuration
          .getPolicyById(policyAuthority.getPolicy())
          .map(Collections::singletonList)
          .orElseGet(Collections::emptyList);
    } else {
      log.warn("Unknown authority type: {}", authority.getClass());
      return Collections.emptyList();
    }
  }
}
