package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationManager;

/**
 * Authentication Manager Resolver
 *
 * @author limaofeng
 */
public interface AuthenticationManagerResolver<C> {

    /**
     * 查询 AuthenticationManager
     *
     * @param context
     * @return
     */
    AuthenticationManager resolve(C context);

}
