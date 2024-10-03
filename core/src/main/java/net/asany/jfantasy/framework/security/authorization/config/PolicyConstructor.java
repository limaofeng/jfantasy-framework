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

import net.asany.jfantasy.framework.security.authorization.policy.PermissionResource;
import net.asany.jfantasy.framework.security.authorization.policy.PolicyEffect;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceActionType;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class PolicyConstructor extends Constructor {
  public PolicyConstructor() {
    super(AuthorizationConfiguration.class);
    this.yamlClassConstructors.put(NodeId.scalar, new ConstructCustom());
  }

  private class ConstructCustom extends Constructor.ConstructScalar {
    @Override
    public Object construct(Node node) {
      if (node.getType() == PolicyEffect.class) {
        return PolicyEffect.fromString(((ScalarNode) node).getValue());
      }
      if (node.getType() == PermissionResource.class) {
        return PermissionResource.parse(((ScalarNode) node).getValue());
      }
      if (node.getType() == ResourceActionType.class) {
        return ResourceActionType.of(((ScalarNode) node).getValue());
      }
      //noinspection VulnerableCodeUsages
      return super.construct(node);
    }
  }
}
