package net.asany.jfantasy.framework.security.auth.oauth2.server;

import java.util.Collections;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

/**
 * 不记名令牌的身份验证令牌
 *
 * @author limaofeng
 */
@Getter
public class BearerTokenAuthenticationToken extends AbstractAuthenticationToken {

  private final AuthType authType;
  private final String token;

  public BearerTokenAuthenticationToken(String token) {
    super(Collections.emptyList());
    Assert.hasText(token, "token cannot be empty");
    this.authType = AuthType.BEARER;
    this.token = token;
  }

  public BearerTokenAuthenticationToken(String token, AuthenticationDetails details) {
    this(token);
    this.details = details;
  }

  public BearerTokenAuthenticationToken(AuthType authType, String token) {
    super(Collections.emptyList());
    Assert.hasText(token, "token cannot be empty");
    this.authType = authType;
    this.token = token;
  }

  @Override
  public Object getCredentials() {
    return this.getToken();
  }

  @Override
  public String getPrincipal() {
    return this.token;
  }
}
