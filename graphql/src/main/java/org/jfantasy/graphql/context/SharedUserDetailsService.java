package org.jfantasy.graphql.context;

import org.jfantasy.framework.security.core.userdetails.SimpleUserDetailsService;

/**
 * GraphQL 身份获取服务
 *
 * @author limaofeng
 */
public interface SharedUserDetailsService extends SimpleUserDetailsService<String> {
}
