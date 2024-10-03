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

  private List<String> publicPaths;

  private DefaultPolicy defaultPolicy;

  private List<PermissionPolicy> policies;

  private List<ConfigRole> roles;

  private List<ConfigResource> resources;

  private static final List<String> ROOT_TYPES =
      List.of(new String[] {"Query", "Mutation", "Subscription"});

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

  public static ResourceAction DEFAULT_ACTION =
      ConfigResource.ConfigResourceAction.builder()
          .id("__default_action__")
          .arn(new HashSet<>(List.of("__default_action__")))
          .build();

  public static ResourceAction SKIP_ACTION =
      ConfigResource.ConfigResourceAction.builder()
          .id("__skip_action__")
          .arn(new HashSet<>(List.of("__skip_action__")))
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

    if (ROOT_TYPES.stream().anyMatch(operation::startsWith)) {
      actionMap.put(operation, DEFAULT_ACTION);
    } else {
      actionMap.put(operation, SKIP_ACTION);
    }
    return actionMap.get(operation);
  }

  public boolean appliesToPublicPaths(Set<String> paths) {
    for (String path : paths) {
      for (String publicPath : publicPaths) {
        if (PermissionPolicy.subjectMatchesPattern(path, publicPath)) {
          return true;
        }
      }
    }
    return false;
  }
}
