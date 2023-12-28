package net.asany.jfantasy.graphql.gateway.data;

import graphql.execution.MergedField;
import graphql.language.Field;
import graphql.language.FieldDefinition;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.schema.*;
import lombok.Builder;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverride;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverrideField;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;

@Builder
public class GraphQLDefaultOverrideDataFetcher implements GraphQLOverrideDataFetcher<Object> {

  private final FieldDefinition fieldDefinition;
  private final SchemaOverride override;
  private final SchemaOverrideField overrideField;
  private final DataFetcher<?> dataFetcher;

  @Override
  public Object get(DataFetchingEnvironment environment) throws Exception {
    return dataFetcher.get(newOverridingEnvironment(environment));
  }

  private DataFetchingEnvironment newOverridingEnvironment(DataFetchingEnvironment environment) {
    DataFetchingEnvironmentImpl.Builder builder =
        DataFetchingEnvironmentImpl.newDataFetchingEnvironment(environment);

    Field originalField = environment.getField();

    Field.Builder fieldBuilder =
        Field.newField(overrideField.getMapping())
            .alias(overrideField.getAlias(originalField.getAlias()))
            .arguments(originalField.getArguments())
            .directives(originalField.getDirectives());

    if (originalField.getSelectionSet() != null) {
      fieldBuilder.selectionSet(
          transformSelectionSet(originalField.getSelectionSet(), environment.getFieldType()));
    }

    builder.mergedField(MergedField.newMergedField(fieldBuilder.build()).build());
    return builder.build();
  }

  private SelectionSet transformSelectionSet(
      SelectionSet originalSelectionSet, GraphQLOutputType outputType) {
    GraphQLObjectType objectType = (GraphQLObjectType) GraphQLTypeUtils.getSourceType(outputType);
    return SelectionSet.newSelectionSet()
        .selections(
            originalSelectionSet.getSelections().stream()
                .map(item -> transformSelection(item, objectType))
                .toList())
        .additionalData(originalSelectionSet.getAdditionalData())
        .sourceLocation(originalSelectionSet.getSourceLocation())
        .comments(originalSelectionSet.getComments())
        .ignoredChars(originalSelectionSet.getIgnoredChars())
        .build();
  }

  private Selection<?> transformSelection(Selection<?> selection, GraphQLObjectType objectType) {
    if (selection instanceof Field field) {
      GraphQLFieldDefinition fieldDefinition = objectType.getFieldDefinition(field.getName());
      GraphQLOutputType fieldType = fieldDefinition.getType();

      Field.Builder fieldBuilder =
          Field.newField(field.getName())
              .alias(field.getAlias())
              .arguments(field.getArguments())
              .directives(field.getDirectives());

      // 如果有覆盖配置，则使用覆盖配置
      if (override.hasIncludeField(objectType.getName(), field.getName())) {
        SchemaOverrideField overrideField =
            override.getField(objectType.getName(), field.getName());
        fieldBuilder
            .name(overrideField.getMapping())
            .alias(overrideField.getAlias(field.getAlias()));
      }

      // 如果有子节点，则递归处理
      if (field.getSelectionSet() != null) {
        fieldBuilder.selectionSet(transformSelectionSet(field.getSelectionSet(), fieldType));
      }

      return fieldBuilder.build();
    }

    return selection;
  }
}
