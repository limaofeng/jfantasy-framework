package net.asany.jfantasy.autoconfigure;

import net.asany.jfantasy.framework.security.auth.apikey.*;
import net.asany.jfantasy.framework.security.auth.core.TokenServicesFactory;
import net.asany.jfantasy.framework.security.auth.core.TokenStore;
import net.asany.jfantasy.framework.security.auth.oauth2.DefaultTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2Authentication;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthenticationProvider;
import net.asany.jfantasy.framework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * OAuth2 ResourceServer
 *
 * @author limaofeng
 */
@Configuration
public class ResourceServerAutoConfiguration {

  @Bean
  public TokenServicesFactory tokenServiceFactory() {
    TokenServicesFactory tokenServiceFactory = new TokenServicesFactory();

    tokenServiceFactory.registerTokenService(
        UsernamePasswordAuthenticationToken.class, DefaultTokenServices.class);
    tokenServiceFactory.registerTokenService(
        OAuth2Authentication.class, DefaultTokenServices.class);
    tokenServiceFactory.registerTokenService(
        BearerTokenAuthentication.class, DefaultTokenServices.class);
    tokenServiceFactory.registerTokenService(ApiKeyAuthentication.class, ApiKeyServices.class);

    return tokenServiceFactory;
  }

  @Bean
  public BearerTokenAuthenticationProvider bearerTokenAuthenticationProvider(
      TokenServicesFactory tokenServicesFactory) {
    return new BearerTokenAuthenticationProvider(
        tokenServicesFactory.getTokenServices(UsernamePasswordAuthenticationToken.class));
  }

  @Bean
  public TokenStore<ApiKey> apiKeyStore(StringRedisTemplate redisTemplate) {
    return new ApiKeyStore(redisTemplate);
  }

  @Bean
  public ApiKeyAuthenticationProvider apiKeyAuthenticationProvider(
      TokenServicesFactory tokenServicesFactory) {
    return new ApiKeyAuthenticationProvider(
        tokenServicesFactory.getTokenServices(ApiKeyAuthentication.class));
  }
}
