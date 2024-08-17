package net.asany.jfantasy.framework.security.auth;

import net.asany.jfantasy.framework.security.authentication.Authentication;

public interface AuthenticationToken<T> extends Authentication {

  default T getToken() {
    return this.getCredentials();
  }
}
