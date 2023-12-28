package net.asany.jfantasy.framework.security.authentication.dao;

import lombok.Setter;
import net.asany.jfantasy.framework.error.ValidationException;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.InternalAuthenticationServiceException;
import net.asany.jfantasy.framework.security.authentication.SimpleAuthenticationToken;
import net.asany.jfantasy.framework.security.core.userdetails.SimpleUserDetailsService;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;
import net.asany.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;

/**
 * 简单身份验证程序
 *
 * @author limaofeng
 */
public class SimpleAuthenticationProvider
    extends AbstractSimpleUserDetailsAuthenticationProvider<SimpleAuthenticationToken<Object>> {

  @Setter private SimpleUserDetailsService<Object> userDetailsService;

  private final Class<?> authenticationClass;

  public SimpleAuthenticationProvider(Class<?> authentication) {
    this.authenticationClass = authentication;
  }

  @Override
  public boolean supports(@SuppressWarnings("rawtypes") Class authentication) {
    return (this.authenticationClass.isAssignableFrom(authentication));
  }

  @Override
  public UserDetails retrieveUser(SimpleAuthenticationToken<Object> authentication) {
    Object token = determineToken(authentication);
    try {
      UserDetails loadedUser = this.userDetailsService.loadUserByToken(token);
      if (loadedUser == null) {
        throw new InternalAuthenticationServiceException(
            "UserDetailsService returned null, which is an interface contract violation");
      }
      return loadedUser;
    } catch (InternalAuthenticationServiceException
        | UsernameNotFoundException
        | ValidationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
    }
  }

  private Object determineToken(Authentication authentication) {
    return authentication.getCredentials();
  }
}
