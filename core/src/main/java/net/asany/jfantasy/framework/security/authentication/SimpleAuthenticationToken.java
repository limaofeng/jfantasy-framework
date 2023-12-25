package net.asany.jfantasy.framework.security.authentication;

import javax.security.auth.Subject;
import net.asany.jfantasy.framework.security.LoginUser;

/**
 * @author limaofeng
 */
public class SimpleAuthenticationToken<T extends Object> extends AbstractAuthenticationToken {

  private final LoginUser principal;
  private final T credentials;

  public SimpleAuthenticationToken() {
    super(null);
    this.principal = null;
    this.credentials = null;
    setAuthenticated(false);
  }

  public SimpleAuthenticationToken(LoginUser user) {
    super(user.getAuthorities());
    this.principal = user;
    this.credentials = (T) user.getPassword();
    setAuthenticated(true);
  }

  public SimpleAuthenticationToken(LoginUser user, T credentials) {
    super(user.getAuthorities());
    this.principal = user;
    this.credentials = credentials;
    setAuthenticated(true);
  }

  public SimpleAuthenticationToken(T credentials) {
    super(null);
    this.principal = null;
    this.credentials = credentials;
    setAuthenticated(false);
  }

  @Override
  public T getCredentials() {
    return this.credentials;
  }

  @Override
  public Object getPrincipal() {
    return this.principal;
  }

  @Override
  public boolean implies(Subject subject) {
    return false;
  }
}
