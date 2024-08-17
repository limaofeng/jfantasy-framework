package net.asany.jfantasy.framework.security.auth.base;

import java.util.Collections;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

@Getter
public class AnonymousAuthenticationToken extends AbstractAuthenticationToken {

  private final AuthType authType;

  private final String credentials;

  public AnonymousAuthenticationToken(String token) {
    super(Collections.emptyList());
    Assert.hasText(token, "token cannot be empty");
    this.authType = AuthType.BASIC;
    this.credentials = token;
  }

  public AnonymousAuthenticationToken(String token, AuthenticationDetails details) {
    this(token);
    this.details = details;
  }

  @Override
  public String getCredentials() {
    return this.credentials;
  }

  @Override
  public String getPrincipal() {
    return this.credentials;
  }
}
