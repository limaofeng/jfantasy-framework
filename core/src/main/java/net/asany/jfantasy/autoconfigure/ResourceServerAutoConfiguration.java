package net.asany.jfantasy.autoconfigure;

import net.asany.jfantasy.framework.security.auth.apikey.ApiKey;
import net.asany.jfantasy.framework.security.auth.apikey.ApiKeyAuthenticationProvider;
import net.asany.jfantasy.framework.security.auth.apikey.ApiKeyServices;
import net.asany.jfantasy.framework.security.auth.apikey.ApiKeyStore;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.auth.core.ClientDetailsService;
import net.asany.jfantasy.framework.security.auth.core.TokenServiceFactory;
import net.asany.jfantasy.framework.security.auth.core.TokenStore;
import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.DefaultTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * OAuth2 ResourceServer
 *
 * @author limaofeng
 */
@Configuration
public class ResourceServerAutoConfiguration {

  @Bean
  public TokenServiceFactory tokenServiceFactory() {
    TokenServiceFactory tokenServiceFactory = new TokenServiceFactory();
    tokenServiceFactory.registerTokenService(ApiKey.class, ApiKeyServices.class);
    return tokenServiceFactory;
  }

  @Bean
  @ConditionalOnBean({ClientDetailsService.class, TokenStore.class})
  public DefaultTokenServices tokenServices(
      TokenStore<AuthToken> tokenStore,
      ClientDetailsService clientDetailsService,
      TaskExecutor taskExecutor) {
    return new DefaultTokenServices(tokenStore, clientDetailsService, taskExecutor);
  }

  @Bean
  @ConditionalOnBean({ResourceServerTokenServices.class})
  public BearerTokenAuthenticationProvider bearerTokenAuthenticationProvider(
      ResourceServerTokenServices<AuthToken> tokenServices) {
    return new BearerTokenAuthenticationProvider(tokenServices);
  }

  @Bean
  public TokenStore<ApiKey> apiKeyStore(StringRedisTemplate redisTemplate) {
    return new ApiKeyStore(redisTemplate);
  }

  @Bean
  public ApiKeyAuthenticationProvider apiKeyAuthenticationProvider(
      TokenServiceFactory tokenServiceFactory) {
    return new ApiKeyAuthenticationProvider(tokenServiceFactory.getTokenServices(ApiKey.class));
  }
}
