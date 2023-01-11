package org.jfantasy.framework.security.core.userdetails;

import org.jfantasy.framework.security.AuthenticationException;

/**
 * @author limaofeng
 */
public class UsernameNotFoundException extends AuthenticationException {

  public UsernameNotFoundException(String message) {
    super(message);
  }
}
