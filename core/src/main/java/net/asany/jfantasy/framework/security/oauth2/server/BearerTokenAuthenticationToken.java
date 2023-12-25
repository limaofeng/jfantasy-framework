package net.asany.jfantasy.framework.security.oauth2.server;

import java.util.Collections;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

/**
 * 不记名令牌的身份验证令牌
 *
 * @author limaofeng
 */
public class BearerTokenAuthenticationToken extends AbstractAuthenticationToken {

  private final String token;

  public BearerTokenAuthenticationToken(String token) {
    super(Collections.emptyList());
    Assert.hasText(token, "token cannot be empty");
    this.token = token;
  }

  public String getToken() {
    return this.token;
  }

  @Override
  public Object getCredentials() {
    return this.getToken();
  }

  @Override
  public Object getPrincipal() {
    return this.getToken();
  }
}
