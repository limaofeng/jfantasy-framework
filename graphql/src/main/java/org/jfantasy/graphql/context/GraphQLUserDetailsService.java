package org.jfantasy.graphql.context;

import org.jfantasy.framework.security.core.userdetails.SimpleUserDetailsService;
import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;

/**
 * GraphQL 身份获取服务
 *
 * @author limaofeng
 */
public interface GraphQLUserDetailsService extends SimpleUserDetailsService<String> {

}
