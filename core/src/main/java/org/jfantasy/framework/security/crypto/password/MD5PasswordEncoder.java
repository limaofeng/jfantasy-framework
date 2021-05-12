package org.jfantasy.framework.security.crypto.password;

import org.springframework.util.DigestUtils;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-03-28 17:18
 */
public class MD5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return  DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes()).toUpperCase();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes()).toUpperCase().equals(encodedPassword);
    }
}
