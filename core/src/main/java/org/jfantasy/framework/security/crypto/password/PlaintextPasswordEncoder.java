package org.jfantasy.framework.security.crypto.password;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-28 17:18
 */
public class PlaintextPasswordEncoder implements PasswordEncoder {
  @Override
  public String encode(CharSequence rawPassword) {
    return rawPassword.toString();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return rawPassword.toString().equals(encodedPassword);
  }
}
