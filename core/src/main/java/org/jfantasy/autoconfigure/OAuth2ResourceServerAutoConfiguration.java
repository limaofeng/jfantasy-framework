package org.jfantasy.autoconfigure;

import org.jfantasy.framework.security.oauth2.DefaultTokenServices;
import org.jfantasy.framework.security.oauth2.core.ClientDetailsService;
import org.jfantasy.framework.security.oauth2.core.TokenStore;
import org.jfantasy.framework.security.oauth2.core.token.ResourceServerTokenServices;
import org.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

/**
 * OAuth2 ResourceServer
 *
 * @author limaofeng
 */
@Configuration
public class OAuth2ResourceServerAutoConfiguration {

  @Bean
  @ConditionalOnBean({ClientDetailsService.class, TokenStore.class})
  public DefaultTokenServices tokenServices(
      TokenStore tokenStore, ClientDetailsService clientDetailsService, TaskExecutor taskExecutor) {
    return new DefaultTokenServices(tokenStore, clientDetailsService, taskExecutor);
  }

  @Bean
  @ConditionalOnBean({ResourceServerTokenServices.class})
  public BearerTokenAuthenticationProvider bearerTokenAuthenticationProvider(
      ResourceServerTokenServices tokenServices) {
    return new BearerTokenAuthenticationProvider(tokenServices);
  }
}
