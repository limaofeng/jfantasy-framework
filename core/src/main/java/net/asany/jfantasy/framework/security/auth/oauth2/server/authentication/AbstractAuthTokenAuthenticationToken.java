package net.asany.jfantasy.framework.security.auth.oauth2.server.authentication;

import java.util.Collection;
import java.util.Map;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public abstract class AbstractAuthTokenAuthenticationToken<T> extends AbstractAuthenticationToken {

  private final Object principal;

  private final Object credentials;

  private final T token;

  protected AbstractAuthTokenAuthenticationToken(T token) {
    this(token, null);
  }

  protected AbstractAuthTokenAuthenticationToken(
      T token, Collection<GrantedAuthority> authorities) {
    this(token, token, token, authorities);
  }

  protected AbstractAuthTokenAuthenticationToken(
      T token, Object principal, Object credentials, Collection<GrantedAuthority> authorities) {
    super(authorities);
    Assert.notNull(token, "token cannot be null");
    Assert.notNull(principal, "principal cannot be null");
    this.principal = principal;
    this.credentials = credentials;
    this.token = token;
  }

  @Override
  public <P> P getPrincipal() {
    return (P) this.principal;
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  public final T getToken() {
    return this.token;
  }

  public abstract Map<String, Object> getTokenAttributes();
}
