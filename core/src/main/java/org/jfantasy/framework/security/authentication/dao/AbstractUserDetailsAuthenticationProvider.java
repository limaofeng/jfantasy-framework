package org.jfantasy.framework.security.authentication.dao;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.*;
import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.security.core.userdetails.UserDetailsChecker;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.support.MessageSourceAccessor;

/** @author limaofeng */
@Slf4j
public abstract class AbstractUserDetailsAuthenticationProvider
    implements AuthenticationProvider<UsernamePasswordAuthenticationToken> {

  protected MessageSourceAccessor messages;

  private boolean hideUserNotFoundExceptions;

  private UserDetailsChecker preAuthenticationChecks;
  private UserDetailsChecker postAuthenticationChecks;

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }

  /**
   * 查找用户
   *
   * @param username 用户名
   * @param token 密码
   * @return 返回用户
   */
  protected abstract UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken token);

  private String determineUsername(Authentication authentication) {
    return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
  }

  @Override
  public Authentication authenticate(UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    String username = determineUsername(authentication);

    UserDetails user;
    try {
      user = this.retrieveUser(username, authentication);
    } catch (UsernameNotFoundException ex) {
      log.debug("Failed to find user '" + username + "'");
      if (!this.hideUserNotFoundExceptions) {
        throw ex;
      }
      throw new BadCredentialsException(
          this.messages.getMessage(
              "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    }
    this.preAuthenticationChecks.check(user);
    additionalAuthenticationChecks(user, authentication);
    this.postAuthenticationChecks.check(user);
    return createSuccessAuthentication(user, authentication, user);
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

  /**
   * 验证密码
   *
   * @param userDetails
   * @param authentication
   * @throws AuthenticationException
   */
  protected abstract void additionalAuthenticationChecks(
      UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException;

  public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
    this.preAuthenticationChecks = preAuthenticationChecks;
  }

  public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
    this.postAuthenticationChecks = postAuthenticationChecks;
  }

  public static class DefaultPreAuthenticationChecks implements UserDetailsChecker {

    private final MessageSourceAccessor messages;

    public DefaultPreAuthenticationChecks(MessageSourceAccessor messages) {
      this.messages = messages;
    }

    @Override
    public void check(UserDetails user) {
      if (!user.isAccountNonLocked()) {
        throw new LockedException(
            this.messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
      }
      if (!user.isEnabled()) {
        throw new DisabledException(
            this.messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
      }
      if (!user.isAccountNonExpired()) {
        throw new AccountExpiredException(
            this.messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
      }
    }
  }

  public static class DefaultPostAuthenticationChecks implements UserDetailsChecker {

    private final MessageSourceAccessor messages;

    public DefaultPostAuthenticationChecks(MessageSourceAccessor messages) {
      this.messages = messages;
    }

    @Override
    public void check(UserDetails user) {
      if (!user.isCredentialsNonExpired()) {
        throw new CredentialsExpiredException(
            this.messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                "User credentials have expired"));
      }
    }
  }

  public void setMessages(MessageSourceAccessor messages) {
    this.messages = messages;
  }

  public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
    this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
  }

  public UserDetailsChecker getPreAuthenticationChecks() {
    return preAuthenticationChecks;
  }

  public UserDetailsChecker getPostAuthenticationChecks() {
    return postAuthenticationChecks;
  }
}
