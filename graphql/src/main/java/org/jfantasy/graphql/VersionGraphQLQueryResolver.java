package org.jfantasy.graphql;

import graphql.kickstart.tools.GraphQLQueryResolver;

/**
 * @author limaofeng
 * @version V1.0
 * 
 * @date 2020/5/18 2:02 下午
 */
public class VersionGraphQLQueryResolver implements GraphQLQueryResolver {

    public String vesion() {
        return System.getenv("VERSION_NUMBER");
    }

}
