package net.asany.jfantasy.framework.security.authorization;

import java.util.List;
import lombok.Builder;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicy;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicyManager;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContext;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;

@Builder
public class DefaultAuthorizationService implements AuthorizationService {

  private PermissionPolicyManager permissionPolicyManager;

  private RequestContextFactory requestContextFactory;

  @Override
  public boolean hasPermission(String resource, String operation, Authentication authentication) {
    if (!authentication.isAuthenticated()) {
      return false;
    }

    Object principal = authentication.getPrincipal();

    RequestContext requestContext = requestContextFactory.create(authentication);

    if (principal instanceof UserDetails) {
      return hasPermissionForUser(resource, operation, (UserDetails) principal, requestContext);
    }

    return false;
  }

  private boolean hasPermissionForUser(
      String resource, String action, UserDetails principal, RequestContext requestContext) {
    List<PermissionPolicy> policies =
        permissionPolicyManager.getPoliciesForUser(principal.getUsername());
    return policies.stream()
        .anyMatch(policy -> policy.hasPermission(resource, action, requestContext));
  }
}
