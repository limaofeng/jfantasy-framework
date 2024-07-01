package net.asany.jfantasy.graphql.gateway.security;

import java.util.List;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authorization.DefaultAuthorizationService;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicy;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicyManager;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionStatement;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;

public class GraphQLAuthorizationService extends DefaultAuthorizationService {

  public GraphQLAuthorizationService(
      PermissionPolicyManager permissionPolicyManager,
      RequestContextFactory requestContextFactory) {
    super(permissionPolicyManager, requestContextFactory);
  }

  @Override
  public boolean hasPermission(String resource, String operation, Authentication authentication) {
    List<PermissionPolicy> policies =
        this.permissionPolicyManager.loadPolicies(authentication.getCredentials());
    for (PermissionPolicy policy : policies) {
      List<PermissionStatement> statements =
          policy.getStatements().stream()
              .filter(this::hasOperationPermission)
              //              .filter(statement -> hasResourceAccess(statement, paths))
              .toList();

      //      Map<Class<?>, List<PropertyFilter>> entityFilter = new HashMap<>();

    }

    return super.hasPermission(resource, operation, authentication);
  }

  private boolean hasOperationPermission(PermissionStatement statement) {
    return statement.getAction().stream().anyMatch(action -> RegexpUtil.wildMatch(action, ""));
  }
}
