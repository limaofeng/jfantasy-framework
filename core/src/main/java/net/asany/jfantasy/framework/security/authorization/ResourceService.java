package net.asany.jfantasy.framework.security.authorization;

import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;

public interface ResourceService {

  /**
   * Get the resource action for the given operation.
   *
   * @param operation the operation
   * @return the resource action
   */
  ResourceAction getResourceAction(String operation);
}
