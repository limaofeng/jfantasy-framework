package org.jfantasy.framework.security.core.userdetails;

import org.jfantasy.framework.security.core.userdetails.UserDetails;

/**
 * @author limaofeng
 */
public interface UserDetailsChecker {

    /**
     * 检查方法
     *
     * @param user
     */
    void check(UserDetails user);

}
