package net.asany.jfantasy.framework.security.core.userdetails;

import net.asany.jfantasy.framework.security.AuthenticationException;

/**
 * @author limaofeng
 */
public class UsernameNotFoundException extends AuthenticationException {

  public UsernameNotFoundException(String message) {
    super(message);
  }
}
