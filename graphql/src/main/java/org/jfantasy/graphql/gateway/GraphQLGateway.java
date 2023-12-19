package org.jfantasy.graphql.gateway;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.jfantasy.graphql.gateway.data.GraphQLGatewayDataFetcherFactory;
import org.jfantasy.graphql.gateway.service.GraphQLService;
import org.jfantasy.graphql.util.GraphQLUtils;

public class GraphQLGateway {

  private final List<GraphQLService> serviceList;

  @Getter private GraphQLSchema schema;

  public GraphQLGateway(List<GraphQLService> serviceList) {
    this.serviceList = serviceList;
  }

  public void init() throws IOException {
    GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();
    List<GraphQLSchema> schemas = new ArrayList<>();

    for (GraphQLService service : serviceList) {
      GraphQLSchema schema = service.makeSchema();

      schemas.add(schema);
    }

    codeRegistryBuilder.defaultDataFetcher(new GraphQLGatewayDataFetcherFactory());

    this.schema = GraphQLUtils.mergeSchemas(schemas, codeRegistryBuilder.build());
  }

  public void destroy() {}
}
