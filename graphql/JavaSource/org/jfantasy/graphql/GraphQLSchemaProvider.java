package org.jfantasy.graphql;

import graphql.schema.GraphQLSchema;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-01 15:12
 */
public interface GraphQLSchemaProvider {

    GraphQLSchema buildSchema();

}
