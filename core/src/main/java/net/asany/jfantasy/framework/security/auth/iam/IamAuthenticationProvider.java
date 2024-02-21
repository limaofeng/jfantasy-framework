package net.asany.jfantasy.framework.security.auth.iam;

import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthenticationProvider;

public class IamAuthenticationProvider extends BearerTokenAuthenticationProvider {
  public IamAuthenticationProvider(ResourceServerTokenServices tokenServices) {
    super(tokenServices);
  }
}
