package net.asany.jfantasy.framework.security.authorization.config;

import java.util.*;
import lombok.Data;
import lombok.SneakyThrows;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicy;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

@Data
public class AuthorizationConfiguration {

  private DefaultPolicy defaultPolicy;

  private List<PermissionPolicy> policies;

  private List<ConfigRole> roles;

  private List<ConfigResource> resources;

  @SneakyThrows
  public static AuthorizationConfiguration load(Resource resource) {
    Constructor constructor = new PolicyConstructor();
    PropertyUtils propertyUtils = new PolicyPropertyUtils();
    propertyUtils.setSkipMissingProperties(true);
    constructor.setPropertyUtils(propertyUtils);

    Yaml yaml = new Yaml(constructor);
    //noinspection VulnerableCodeUsages
    return yaml.load(resource.getInputStream());
  }

  public List<PermissionPolicy> getPoliciesForUser(String username) {
    return this.policies;
  }

  public List<PermissionPolicy> getPolicyForRole(String role) {
    return this.policies.stream().filter(p -> p.appliesToSubject("role:" + role)).toList();
  }

  public Optional<PermissionPolicy> getPolicyById(String id) {
    return this.policies.stream().filter(p -> p.getId().equals(id)).findFirst();
  }

  private Map<String, ResourceAction> actionMap = new HashMap<>();

  private ResourceAction EMPTY_ACTION =
      ConfigResource.ConfigResourceAction.builder()
          .id("none")
          .arn(new HashSet<>(List.of("*")))
          .build();

  public ResourceAction getResourceActionForOperation(String operation) {
    if (actionMap.containsKey(operation)) {
      return actionMap.get(operation);
    }

    for (ConfigResource resource : this.resources) {
      for (ResourceAction action : resource.getActions()) {
        if (action.getOperations().contains(operation)) {
          actionMap.put(operation, action);
          return action;
        }
      }
    }

    actionMap.put(operation, EMPTY_ACTION);
    return EMPTY_ACTION;
  }
}
