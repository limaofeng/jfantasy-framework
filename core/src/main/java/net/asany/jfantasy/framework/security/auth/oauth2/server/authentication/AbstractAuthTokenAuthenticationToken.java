package net.asany.jfantasy.framework.security.auth.oauth2.server.authentication;

import java.util.Collection;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public abstract class AbstractAuthTokenAuthenticationToken<P, C>
    extends AbstractAuthenticationToken<AuthToken> {

  private final P principal;

  private final C credentials;

  protected AbstractAuthTokenAuthenticationToken(
      P principal, C credentials, Collection<GrantedAuthority> authorities) {
    super(authorities);
    Assert.notNull(principal, "principal cannot be null");
    this.principal = principal;
    this.credentials = credentials;
  }

  @Override
  public P getPrincipal() {
    return this.principal;
  }

  @Override
  public C getCredentials() {
    return this.credentials;
  }

  public abstract Map<String, Object> getTokenAttributes();
}
