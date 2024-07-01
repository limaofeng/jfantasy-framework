package net.asany.jfantasy.framework.security.authorization;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.authentication.SimpleAuthenticationToken;
import net.asany.jfantasy.framework.security.authorization.policy.config.Configuration;
import net.asany.jfantasy.framework.security.authorization.policy.config.ConfigurationPermissionPolicyManager;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.security.authorization.policy.context.WebRequestContextBuilder;
import net.asany.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

@Slf4j
class DefaultAuthorizationServiceTest {

  private AuthorizationService authorizationService;

  @BeforeEach
  public void setUp() throws Exception {
    RequestContextFactory requestContextFactory =
        new RequestContextFactory(List.of(new WebRequestContextBuilder()));

    Configuration configuration =
        Configuration.load(
            "/Users/limaofeng/Workspace/framework/graphql-gateway/src/test/resources/auth-policy.yaml");

    ConfigurationPermissionPolicyManager permissionPolicyManager =
        new ConfigurationPermissionPolicyManager(configuration);

    authorizationService =
        new DefaultAuthorizationService(permissionPolicyManager, requestContextFactory);
  }

  @AfterEach
  public void tearDown() throws Exception {}

  @Test
  void hasPermission() {
    SimpleAuthenticationToken<?> authentication =
        new SimpleAuthenticationToken<>(LoginUser.builder().name("xxx").build(), "password");
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    boolean hasPermission = authorizationService.hasPermission("/users/1", "read", authentication);
    log.info("hasPermission: {}", hasPermission);
  }
}
