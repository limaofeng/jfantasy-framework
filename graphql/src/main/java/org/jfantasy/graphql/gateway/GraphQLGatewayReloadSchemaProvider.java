package org.jfantasy.graphql.gateway;

import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.schema.GraphQLSchema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.HandshakeRequest;
import java.io.IOException;

/**
 * 默认的 GraphQLReloadSchemaProvider
 *
 * @author limaofeng
 */
public class GraphQLGatewayReloadSchemaProvider implements GraphQLReloadSchemaProvider {

  private final GraphQLGateway gateway;
  private GraphQLSchema graphQLSchema;
  private GraphQLSchema readOnlySchema;

  public GraphQLGatewayReloadSchemaProvider(GraphQLGateway gateway) {
    this.gateway = gateway;

    this.graphQLSchema = gateway.getSchema();
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
    this.graphQLSchema = gateway.getSchema();
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
