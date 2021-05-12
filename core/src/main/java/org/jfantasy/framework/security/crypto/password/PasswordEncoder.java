package org.jfantasy.framework.security.crypto.password;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-03-28 15:00
 */
public interface PasswordEncoder {

    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);
}
