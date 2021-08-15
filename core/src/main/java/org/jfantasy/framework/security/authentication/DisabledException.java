package org.jfantasy.framework.security.authentication;

public class DisabledException extends AccountStatusException {

  public DisabledException(String msg) {
    super(msg);
  }
}
