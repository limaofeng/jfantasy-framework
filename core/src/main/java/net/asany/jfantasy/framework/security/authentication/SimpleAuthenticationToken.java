// package net.asany.jfantasy.framework.security.authentication;
//
// import javax.security.auth.Subject;
// import net.asany.jfantasy.framework.security.LoginUser;
//
/// **
// * 简单身份验证令牌
// *
// * <p>该类实现了 Authentication 接口，提供了 Authentication 接口的基本实现。
// *
// * @author limaofeng
// */
// public class SimpleAuthenticationToken<T> extends AbstractAuthenticationToken {
//
//  private final Object principal;
//  private final T credentials;
//
//  public SimpleAuthenticationToken() {
//    super(null);
//    this.principal = null;
//    this.credentials = null;
//    setAuthenticated(false);
//  }
//
//  public SimpleAuthenticationToken(ApiKeyPrincipal principal) {
//    super(principal.getAuthorities());
//    this.principal = principal;
//    this.credentials = null;
//    setAuthenticated(true);
//  }
//
//  public SimpleAuthenticationToken(LoginUser user) {
//    super(user.getAuthorities());
//    this.principal = user;
//    //noinspection unchecked
//    this.credentials = (T) user.getPassword();
//    setAuthenticated(true);
//  }
//
//  public SimpleAuthenticationToken(LoginUser user, T credentials) {
//    super(user.getAuthorities());
//    this.principal = user;
//    this.credentials = credentials;
//    setAuthenticated(true);
//  }
//
//  public SimpleAuthenticationToken(T credentials) {
//    super(null);
//    this.principal = null;
//    this.credentials = credentials;
//    setAuthenticated(false);
//  }
//
//  @Override
//  public T getCredentials() {
//    return this.credentials;
//  }
//
//  @Override
//  public <P> P getPrincipal() {
//    //noinspection unchecked
//    return (P) this.principal;
//  }
//
//  @Override
//  public boolean implies(Subject subject) {
//    return false;
//  }
// }
