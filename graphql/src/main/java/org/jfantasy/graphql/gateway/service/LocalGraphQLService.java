package org.jfantasy.graphql.gateway.service;

import graphql.language.TypeDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import java.io.IOException;
import lombok.Builder;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.graphql.gateway.config.GraphQLServiceOverride;
import org.jfantasy.graphql.gateway.config.GraphQLServiceOverrideType;

@Builder
public class LocalGraphQLService implements GraphQLService {

  private final GraphQLSchema schema;

  private GraphQLServiceOverride override;

  @Override
  public String getName() {
    return "local";
  }

  @Override
  public GraphQLServiceOverride getOverrideConfig() {
    return this.override;
  }

  @Override
  public GraphQLSchema makeSchema() throws IOException {
    return schema;
  }

  @Override
  public GraphQLInputType getInputType(String name) {
    return (GraphQLInputType) schema.getType(name);
  }

  @Override
  public TypeDefinition<?> getTypeDefinition(String name) {
    return ClassUtil.getValue(schema.getType(name), "definition");
  }

  @Override
  public GraphQLOutputType getOutputType(String name) {
    return ClassUtil.getValue(schema.getType(name), "definition");
  }

  @Override
  public boolean hasType(String name) {
    return false;
  }

  @Override
  public void addType(String name, GraphQLType build) {}

  public static class LocalGraphQLServiceBuilder {

    public LocalGraphQLServiceBuilder ignoreField(String typeName, String name) {
      GraphQLServiceOverrideType overrideType = getOverrideType(typeName);
      overrideType.addIgnoreField(name);
      return this;
    }

    public LocalGraphQLServiceBuilder renameField(String typeName, String name, String newName) {
      GraphQLServiceOverrideType overrideType = getOverrideType(typeName);
      overrideType.renameField(name, newName);
      return this;
    }

    public LocalGraphQLServiceBuilder renameFieldArgument(
        String typeName, String fieldName, String name, String newName) {
      GraphQLServiceOverrideType overrideType = getOverrideType(typeName);
      overrideType.renameFieldArgument(fieldName, name, newName);
      return this;
    }

    private GraphQLServiceOverrideType getOverrideType(String typeName) {
      if (this.override == null) {
        this.override = GraphQLServiceOverride.builder().build();
      }
      return this.override
          .getOverrideConfigForType(typeName)
          .orElseGet(() -> this.override.createOverrideConfig(typeName));
    }

    public LocalGraphQLServiceBuilder ignoreField(String name) {
      String typeName = name.split("\\.")[0];
      String fieldName = name.split("\\.")[1];
      return ignoreField(typeName, fieldName);
    }
  }
}
