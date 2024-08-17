// package net.asany.jfantasy.framework.security.authentication.dao;
//
// import lombok.Setter;
// import net.asany.jfantasy.framework.error.ValidationException;
// import net.asany.jfantasy.framework.security.AuthenticationException;
// import net.asany.jfantasy.framework.security.authentication.Authentication;
// import
// net.asany.jfantasy.framework.security.authentication.InternalAuthenticationServiceException;
// import net.asany.jfantasy.framework.security.authentication.SimpleAuthenticationToken;
// import net.asany.jfantasy.framework.security.core.userdetails.SimpleUserDetailsService;
// import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;
// import net.asany.jfantasy.framework.security.core.userdetails.UserDetailsChecker;
// import net.asany.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.context.support.MessageSourceAccessor;
//
/// **
// * 简单身份验证程序
// *
// * @author limaofeng
// */
// public class SimpleAuthenticationProvider
//    extends AbstractUserDetailsAuthenticationProvider<SimpleAuthenticationToken<Object>> {
//
//  @Setter private SimpleUserDetailsService<Object> userDetailsService;
//
//  private final Class<?> authenticationClass;
//
//  public SimpleAuthenticationProvider(
//      Class<? extends Authentication> authenticationClass,
//      MessageSourceAccessor messages,
//      boolean hideUserNotFoundExceptions,
//      UserDetailsChecker preAuthenticationChecks,
//      UserDetailsChecker postAuthenticationChecks) {
//    super(messages, hideUserNotFoundExceptions, preAuthenticationChecks,
// postAuthenticationChecks);
//    this.authenticationClass = authenticationClass;
//  }
//
//  @Override
//  public boolean supports(Class<? extends Authentication> authentication) {
//    return (this.authenticationClass.isAssignableFrom(authentication));
//  }
//
//  @Override
//  protected UserDetails retrieveUser(
//      String username, SimpleAuthenticationToken<Object> authenticationToken) {
//    Object token = authenticationToken.getCredentials();
//    try {
//      UserDetails loadedUser = this.userDetailsService.loadUserByToken(token);
//      if (loadedUser == null) {
//        throw new InternalAuthenticationServiceException(
//            "UserDetailsService returned null, which is an interface contract violation");
//      }
//      return loadedUser;
//    } catch (InternalAuthenticationServiceException
//        | UsernameNotFoundException
//        | ValidationException ex) {
//      throw ex;
//    } catch (Exception ex) {
//      throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
//    }
//  }
//
//  @Override
//  protected void additionalAuthenticationChecks(
//      UserDetails userDetails, SimpleAuthenticationToken<Object> authentication)
//      throws AuthenticationException {}
// }
