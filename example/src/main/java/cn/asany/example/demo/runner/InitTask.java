/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
