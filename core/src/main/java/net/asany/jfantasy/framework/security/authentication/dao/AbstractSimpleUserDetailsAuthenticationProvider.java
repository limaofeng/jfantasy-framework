package net.asany.jfantasy.framework.security.authentication.dao;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.authentication.*;
import net.asany.jfantasy.framework.security.core.userdetails.DefaultAuthenticationChecks;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetailsChecker;
import net.asany.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author limaofeng
 */
@Slf4j
public abstract class AbstractSimpleUserDetailsAuthenticationProvider<
        T extends SimpleAuthenticationToken>
    implements AuthenticationProvider<T> {

  private boolean hideUserNotFoundExceptions;

  private UserDetailsChecker preAuthenticationChecks = new DefaultAuthenticationChecks();
  private UserDetailsChecker postAuthenticationChecks = new DefaultAuthenticationChecks();

  public abstract UserDetails retrieveUser(SimpleAuthenticationToken token);

  private String determineToken(Authentication authentication) {
    return (String) authentication.getCredentials();
  }

  @Override
  public Authentication authenticate(SimpleAuthenticationToken token)
      throws AuthenticationException {
    UserDetails user;
    try {
      user = this.retrieveUser(token);
    } catch (UsernameNotFoundException ex) {
      log.debug("Failed to find user '" + token.getName() + "'");
      if (!this.hideUserNotFoundExceptions) {
        throw ex;
      }
      throw new BadCredentialsException("Bad credentials");
    }
    this.preAuthenticationChecks.check(user);
    this.postAuthenticationChecks.check(user);
    Object principalToReturn = user;
    return createSuccessAuthentication(principalToReturn, token, user);
  }

  protected Authentication createSuccessAuthentication(
      Object principal, Authentication authentication, UserDetails user) {
    UsernamePasswordAuthenticationToken result =
        new UsernamePasswordAuthenticationToken(
            principal, authentication.getCredentials(), user.getAuthorities());
    result.setDetails(authentication.getDetails());
    log.debug("Authenticated user");
    return result;
  }

  public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
    this.preAuthenticationChecks = preAuthenticationChecks;
  }

  public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
    this.postAuthenticationChecks = postAuthenticationChecks;
  }
}
