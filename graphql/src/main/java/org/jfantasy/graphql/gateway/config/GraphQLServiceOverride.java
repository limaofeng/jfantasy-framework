package org.jfantasy.graphql.gateway.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;

@Builder
public class GraphQLServiceOverride {

  @Builder.Default Map<String, GraphQLServiceOverrideType> overrides = new HashMap<>();

  public Optional<GraphQLServiceOverrideType> getOverrideConfigForType(String typeName) {
    return Optional.ofNullable(overrides.get(typeName));
  }

  public GraphQLServiceOverrideType createOverrideConfig(String typeName) {
    GraphQLServiceOverrideType overrideType =
        GraphQLServiceOverrideType.builder().type(typeName).build();
    overrides.put(typeName, overrideType);
    return overrides.get(typeName);
  }

  public boolean isFieldRenamed(String typeName, String fieldName) {
    return getOverrideConfigForType(typeName)
        .map(type -> type.getNewFields().get(fieldName))
        .map(GraphQLServiceOverrideField::getRename)
        .isPresent();
  }

  public String getOriginalFieldName(String typeName, String fieldName) {
    return getOverrideConfigForType(typeName)
        .map(type -> type.getNewFields().get(fieldName))
        .map(GraphQLServiceOverrideField::getName)
        .orElse(fieldName);
  }

  public boolean isFieldArgumentRenamed(String typeName, String fieldName, String argumentName) {
    return getOverrideConfigForType(typeName)
        .map(type -> type.getNewFields().get(fieldName))
        .map(field -> field.getNewArguments().get(argumentName))
        .map(GraphQLServiceOverrideFieldArgument::getRename)
        .isPresent();
  }

  public boolean hasFieldRename(String typeName, String fieldName) {
    Optional<GraphQLServiceOverrideType> overrideType = getOverrideConfigForType(typeName);
    return overrideType
        .map(type -> type.getFields().get(fieldName))
        .map(GraphQLServiceOverrideField::getRename)
        .isPresent();
  }

  public boolean shouldIgnoreField(String typeName, String fieldName) {
    Optional<GraphQLServiceOverrideType> overrideType = this.getOverrideConfigForType(typeName);
    return overrideType
        .map(type -> type.getFields().get(fieldName))
        .map(GraphQLServiceOverrideField::isIgnore)
        .orElse(false);
  }

  public String getFieldRename(String typeName, String fieldName) {
    Optional<GraphQLServiceOverrideType> overrideType = getOverrideConfigForType(typeName);
    return overrideType
        .map(type -> type.getFields().get(fieldName))
        .map(GraphQLServiceOverrideField::getRename)
        .orElse(fieldName);
  }

  public String getFieldArgumentRename(String typeName, String fieldName, String argumentName) {
    Optional<GraphQLServiceOverrideType> overrideType = getOverrideConfigForType(typeName);
    return overrideType
        .map(type -> type.getFields().get(fieldName))
        .map(field -> field.getArguments().get(argumentName))
        .map(GraphQLServiceOverrideFieldArgument::getRename)
        .orElse(argumentName);
  }

  public String getOriginalFieldArgumentName(
      String typeName, String fieldName, String argumentName) {
    String originalFieldName = getOriginalFieldName(typeName, fieldName);
    return getOverrideConfigForType(typeName)
        .map(type -> type.getFields().get(originalFieldName))
        .map(field -> field.getNewArguments().get(argumentName))
        .map(GraphQLServiceOverrideFieldArgument::getName)
        .orElse(argumentName);
  }
}
