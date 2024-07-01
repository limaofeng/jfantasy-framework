package net.asany.jfantasy.framework.security.authorization.policy.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authorization.PolicyBasedAuthorizationProvider;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicy;
import net.asany.jfantasy.framework.security.authorization.policy.PolicyEffect;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContext;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.PolicyAuthority;
import net.asany.jfantasy.framework.security.core.authority.RoleAuthority;

@Slf4j
public class ConfigurationPolicyBasedAuthorizationProvider
    implements PolicyBasedAuthorizationProvider {

  private final Configuration configuration;
  private final RequestContextFactory requestContextFactory;

  public ConfigurationPolicyBasedAuthorizationProvider(
      RequestContextFactory requestContextFactory, Configuration configuration) {
    this.configuration = configuration;
    this.requestContextFactory = requestContextFactory;
  }

  @Override
  public boolean authorize(String resource, String action, Authentication authentication) {
    if (!authentication.isAuthenticated()) {
      return configuration.getDefaultPolicy().getEffect() == PolicyEffect.ALLOW;
    }

    Collection<GrantedAuthority> authorities = authentication.getAuthorities();

    RequestContext requestContext = requestContextFactory.create(authentication);

    boolean isAuthorized =
        authorities.stream()
            .anyMatch(
                authority -> {
                  List<PermissionPolicy> policies = getPoliciesForAuthority(authority);
                  return policies.stream()
                      .anyMatch(policy -> policy.hasPermission(resource, action, requestContext));
                });

    // 只有在没有匹配的规则时，才考虑默认策略
    if (requestContext.getMatchedRules().isEmpty()) {
      log.warn("No rules matched for resource: {}, action: {}", resource, action);
      return configuration.getDefaultPolicy().getEffect().isAllow();
    }

    log.info("Matched rules: {}", requestContext.getMatchedRules());
    return isAuthorized;
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
