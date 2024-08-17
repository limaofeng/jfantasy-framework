package cn.asany.example.demo.runner;

import net.asany.jfantasy.framework.security.auth.apikey.ApiKey;
import net.asany.jfantasy.framework.security.auth.apikey.ApiKeyAuthentication;
import net.asany.jfantasy.framework.security.auth.core.TokenServicesFactory;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import org.springframework.boot.CommandLineRunner;

public class InitTask implements CommandLineRunner {

  private final TokenServicesFactory tokenServicesFactory;

  public InitTask(TokenServicesFactory tokenServicesFactory) {
    this.tokenServicesFactory = tokenServicesFactory;
  }

  @Override
  public void run(String... args) throws Exception {
    // TODO Auto-generated method stub
    System.out.println("InitTask");

    AuthorizationServerTokenServices<ApiKey> tokenServices =
        tokenServicesFactory.getTokenServices(ApiKeyAuthentication.class);

    //    ApiKeyAuthentication authentication =
    //        AuthTokenUtils.buildApiKey("test",
    // ApiKeyPrincipal.builder().name("123123213").build());
    //    ApiKey apiKey = tokenServices.createAccessToken(authentication);
    //    System.out.println(apiKey.getTokenValue());
  }
}
