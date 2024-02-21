package cn.asany.example.demo.runner;

import net.asany.jfantasy.framework.security.auth.apikey.ApiKey;
import net.asany.jfantasy.framework.security.auth.apikey.ApiKeyAuthentication;
import net.asany.jfantasy.framework.security.auth.core.TokenServiceFactory;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import net.asany.jfantasy.framework.security.auth.utils.AuthTokenUtils;
import net.asany.jfantasy.framework.security.authentication.ApiKeyPrincipal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitTask implements CommandLineRunner {

  private final TokenServiceFactory tokenServiceFactory;

  public InitTask(TokenServiceFactory tokenServiceFactory) {
    this.tokenServiceFactory = tokenServiceFactory;
  }

  @Override
  public void run(String... args) throws Exception {
    // TODO Auto-generated method stub
    System.out.println("InitTask");

    AuthorizationServerTokenServices<ApiKey> tokenServices =
        tokenServiceFactory.getTokenServices(ApiKey.class);

    ApiKeyAuthentication authentication =
        AuthTokenUtils.buildApiKey("test", ApiKeyPrincipal.builder().name("123123213").build());
    ApiKey apiKey = tokenServices.createAccessToken(authentication);
    System.out.println(apiKey.getTokenValue());
  }
}
