package net.asany.jfantasy.framework.security.authentication.dao;

import net.asany.jfantasy.framework.error.ValidationException;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.BadCredentialsException;
import net.asany.jfantasy.framework.security.authentication.InternalAuthenticationServiceException;
import net.asany.jfantasy.framework.security.authentication.UsernamePasswordAuthenticationToken;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetailsChecker;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetailsService;
import net.asany.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import net.asany.jfantasy.framework.security.crypto.password.PasswordEncoder;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author limaofeng
 */
public class DaoAuthenticationProvider
    extends AbstractUserDetailsAuthenticationProvider<UsernamePasswordAuthenticationToken> {

  private final UserDetailsService<? extends UserDetails> userDetailsService;

  private final PasswordEncoder passwordEncoder;

  public DaoAuthenticationProvider(
      UserDetailsService<? extends UserDetails> userDetailsService,
      PasswordEncoder passwordEncoder,
      MessageSourceAccessor messages,
      boolean hideUserNotFoundExceptions,
      UserDetailsChecker preAuthenticationChecks,
      UserDetailsChecker postAuthenticationChecks) {
    super(messages, hideUserNotFoundExceptions, preAuthenticationChecks, postAuthenticationChecks);
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }

  @Override
  public UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken token) {
    try {
      UserDetails loadedUser = this.userDetailsService.loadUserByUsername(username);
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

  @Override
  public void additionalAuthenticationChecks(
      UserDetails user, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    if (authentication.getCredentials() == null) {
      throw new BadCredentialsException(
          this.messages.getMessage(
              "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    }
    String presentedPassword = authentication.getCredentials().toString();
    if (!this.passwordEncoder.matches(presentedPassword, user.getPassword())) {
      throw new BadCredentialsException(
          this.messages.getMessage(
              "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    }
  }
}
