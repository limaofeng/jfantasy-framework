package net.asany.jfantasy.graphql.gateway.service;

import graphql.schema.GraphQLSchema;
import java.util.List;
import lombok.Builder;

@Builder(builderClassName = "Builder")
public class LocalGraphQLService implements GraphQLService {

  private final GraphQLSchema schema;

  private List<String> excludeFields;

  @Override
  public String getName() {
    return "local";
  }

  @Override
  public GraphQLSchema makeSchema() {
    return schema;
  }

  public static class Builder {

    public Builder excludeFields(List<String> fields) {
      this.excludeFields = fields;
      return this;
    }

    public Builder excludeFields(String... fields) {
      this.excludeFields = List.of(fields);
      return this;
    }
  }
}
