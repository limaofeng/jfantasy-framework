package net.asany.jfantasy.framework.security.authorization.config;

import net.asany.jfantasy.framework.security.authorization.ResourceService;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;

public class ConfigurationResourceService implements ResourceService {

  private final AuthorizationConfiguration configuration;

  public ConfigurationResourceService(AuthorizationConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public ResourceAction getResourceAction(String s) {
    return null;
  }
}
