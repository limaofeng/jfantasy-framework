package net.asany.jfantasy.framework.security.authorization.config;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

@Slf4j
class AuthorizationConfigurationTest {

  private AuthorizationConfiguration configuration;

  @BeforeEach
  void setUp() {
    FileSystemResource resource =
        new FileSystemResource(
            "/Users/limaofeng/Workspace/framework/graphql-gateway/src/test/resources/auth-policy.yaml");
    this.configuration = AuthorizationConfiguration.load(resource);
  }

  @Test
  void getResourceActionForOperation() {
    ResourceAction resourceAction =
        this.configuration.getResourceActionForOperation("Query.listUsers");
    log.info("resourceAction: {}", resourceAction);
    resourceAction = this.configuration.getResourceActionForOperation("Query.createUsers");
    log.info("resourceAction: {}", resourceAction);
  }
}
