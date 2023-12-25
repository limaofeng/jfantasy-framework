package net.asany.jfantasy.framework.security.crypto.password;

import org.springframework.util.DigestUtils;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-28 17:18
 */
public class MD5PasswordEncoder implements PasswordEncoder {

  @Override
  public String encode(CharSequence rawPassword) {
    return DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes()).toUpperCase();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return encodedPassword.equals(encode(rawPassword));
  }
}
