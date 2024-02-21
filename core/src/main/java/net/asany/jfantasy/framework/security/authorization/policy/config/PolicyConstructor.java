package net.asany.jfantasy.framework.security.authorization.policy.config;

import net.asany.jfantasy.framework.security.authorization.policy.PermissionResource;
import net.asany.jfantasy.framework.security.authorization.policy.PolicyEffect;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class PolicyConstructor extends Constructor {
  public PolicyConstructor() {
    super(Configuration.class);
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
      return super.construct(node);
    }
  }
}
