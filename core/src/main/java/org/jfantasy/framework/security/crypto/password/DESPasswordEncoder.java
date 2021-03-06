package org.jfantasy.framework.security.crypto.password;

import lombok.SneakyThrows;
import org.jfantasy.framework.crypto.DESPlus;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-28 17:18
 */
public class DESPasswordEncoder implements PasswordEncoder {

  private static DESPlus desPlus = new DESPlus("jfantasy.asany.cn");

  @Override
  @SneakyThrows
  public String encode(CharSequence rawPassword) {
    return desPlus.encrypt(rawPassword.toString());
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return encodedPassword.equals(encode(rawPassword));
  }
}
