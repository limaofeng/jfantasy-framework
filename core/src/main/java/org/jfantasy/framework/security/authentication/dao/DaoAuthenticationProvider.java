package org.jfantasy.framework.security.authentication.dao;

import org.jfantasy.framework.error.ValidationException;
import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.BadCredentialsException;
import org.jfantasy.framework.security.authentication.InternalAuthenticationServiceException;
import org.jfantasy.framework.security.authentication.UsernamePasswordAuthenticationToken;
import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.security.core.userdetails.UserDetailsService;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.jfantasy.framework.security.crypto.password.PasswordEncoder;

/** @author limaofeng */
public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  private UserDetailsService<UserDetails> userDetailsService;

  private PasswordEncoder passwordEncoder;

  public DaoAuthenticationProvider() {}

  public DaoAuthenticationProvider(
      UserDetailsService<UserDetails> userDetailsService, PasswordEncoder passwordEncoder) {
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
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

  public void setUserDetailsService(UserDetailsService<UserDetails> userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }
}
