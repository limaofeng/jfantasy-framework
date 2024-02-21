package net.asany.jfantasy.framework.security.auth.oauth2.server;

import java.util.Collections;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

/**
 * 不记名令牌的身份验证令牌
 *
 * @author limaofeng
 */
@Getter
public class BearerTokenAuthenticationToken extends AbstractAuthenticationToken {

  private final TokenType tokenType;
  private final String token;

  public BearerTokenAuthenticationToken(String token) {
    super(Collections.emptyList());
    Assert.hasText(token, "token cannot be empty");
    this.tokenType = TokenType.JWT;
    this.token = token;
  }

  public BearerTokenAuthenticationToken(TokenType tokenType, String token) {
    super(Collections.emptyList());
    Assert.hasText(token, "token cannot be empty");
    this.tokenType = tokenType;
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
