package net.asany.jfantasy.framework.security.authorization.policy.config;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.authentication.SimpleAuthenticationToken;
import net.asany.jfantasy.framework.security.authorization.PolicyBasedAuthorizationProvider;
import net.asany.jfantasy.framework.security.authorization.config.AuthorizationConfiguration;
import net.asany.jfantasy.framework.security.authorization.config.ConfigurationPolicyBasedAuthorizationProvider;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.security.authorization.policy.context.WebRequestContextBuilder;
import net.asany.jfantasy.framework.security.core.authority.AuthorityUtils;
import net.asany.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;

@Slf4j
class ConfigurationPolicyBasedAuthorizationProviderTest {

  private PolicyBasedAuthorizationProvider authorizationProvider;

  @BeforeEach
  void setUp() {
    RequestContextFactory requestContextFactory =
        new RequestContextFactory(List.of(new WebRequestContextBuilder()));

    FileSystemResource resource =
        new FileSystemResource(
            "/Users/limaofeng/Workspace/framework/graphql-gateway/src/test/resources/auth-policy.yaml");

    AuthorizationConfiguration configuration = AuthorizationConfiguration.load(resource);

    authorizationProvider =
        new ConfigurationPolicyBasedAuthorizationProvider(requestContextFactory, configuration);
  }

  @AfterEach
  void tearDown() {}

  @Test
  void authorize() {
    SimpleAuthenticationToken<?> authentication =
        new SimpleAuthenticationToken<>(LoginUser.builder().name("xxx").build(), "password");
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    authentication.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_user"));
    log.info("isAuthenticated: {}", authentication.isAuthenticated());
    log.info(
        "path /users/1, delete, isAuthorized: {}",
        authorizationProvider.authorize("/users/1", "delete", authentication));
  }
}
