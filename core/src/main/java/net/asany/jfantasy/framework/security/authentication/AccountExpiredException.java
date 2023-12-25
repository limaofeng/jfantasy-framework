package net.asany.jfantasy.framework.security.authentication;

public class AccountExpiredException extends AccountStatusException {

  public AccountExpiredException(String msg) {
    super(msg);
  }
}
