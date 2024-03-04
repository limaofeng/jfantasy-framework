package org.jfantasy.graphql.execution.config;

import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.HandshakeRequest;

/**
 * 默认的 GraphQLReloadSchemaProvider
 *
 * @author limaofeng
 */
public class DefaultGraphQLReloadSchemaProvider implements GraphQLReloadSchemaProvider {

  private final SchemaParser schemaParser;
  private GraphQLSchema graphQLSchema;
  private GraphQLSchema readOnlySchema;

  public DefaultGraphQLReloadSchemaProvider(SchemaParser schemaParser) {
    this.schemaParser = schemaParser;

    this.graphQLSchema = schemaParser.makeExecutableSchema();
    this.readOnlySchema = GraphQLSchemaProvider.copyReadOnly(this.graphQLSchema);
  }

  @Override
  public GraphQLSchema getSchema() {
    return this.graphQLSchema;
  }

  @Override
  public GraphQLSchema getReadOnlySchema() {
    return this.readOnlySchema;
  }

  @Override
  public void updateSchema() throws IOException {
    this.graphQLSchema = schemaParser.makeExecutableSchema();
    this.readOnlySchema = GraphQLSchemaProvider.copyReadOnly(this.graphQLSchema);
  }

  @Override
  public GraphQLSchema getSchema(HttpServletRequest request) {
    return this.graphQLSchema;
  }

  @Override
  public GraphQLSchema getSchema(HandshakeRequest request) {
    return this.graphQLSchema;
  }

  @Override
  public GraphQLSchema getReadOnlySchema(HttpServletRequest request) {
    return this.readOnlySchema;
  }
}
