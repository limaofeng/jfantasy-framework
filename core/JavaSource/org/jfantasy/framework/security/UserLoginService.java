package org.jfantasy.framework.security;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-03 20:24
 */
public interface UserLoginService {

    UserDetails login(String username, String password);

}
