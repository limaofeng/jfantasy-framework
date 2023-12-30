package net.asany.jfantasy.graphql.gateway.data;

import graphql.execution.MergedField;
import graphql.language.*;
import graphql.schema.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import net.asany.jfantasy.graphql.gateway.config.FieldResolve;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverride;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverrideField;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverrideFieldArgument;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;
import net.asany.jfantasy.graphql.gateway.util.GraphQLValueUtils;

@Builder(builderClassName = "Builder")
public class GraphQLDefaultOverrideDataFetcher implements DataFetcher<Object> {

  private final SchemaOverride override;
  private final SchemaOverrideField overrideField;
  private final DataFetcher<?> dataFetcher;
  private final FieldResolve resolve;

  @Override
  public Object get(DataFetchingEnvironment environment) throws Exception {
    if (resolve != null) {
      return dataFetcher.get(newFileDelegationEnvironment(environment));
    }
    return dataFetcher.get(newOverridingEnvironment(environment));
  }

  private DataFetchingEnvironment newFileDelegationEnvironment(
      DataFetchingEnvironment environment) {
    Field field = environment.getField();

    GraphQLSchema graphQLSchema = environment.getGraphQLSchema();
    GraphQLObjectType objectType = graphQLSchema.getQueryType();

    List<VariableDefinition> variableDefinitions = new ArrayList<>();
    Map<String, Object> variables = new HashMap<>();
    List<Argument> arguments = new ArrayList<>();

    for (Map.Entry<String, String> entry : resolve.getArguments().entrySet()) {
      GraphQLObjectType fieldType = (GraphQLObjectType) environment.getFieldType();
      FieldDefinition fieldDefinition = fieldType.getField(entry.getKey()).getDefinition();
      assert fieldDefinition != null;
      GraphQLOutputType argType =
          (GraphQLOutputType)
              graphQLSchema.getType(
                  GraphQLTypeUtils.getTypeName(overrideField.getOriginalType()).getName());
      Object value =
          GraphQLValueUtils.convert(
              environment.getSource(),
              entry.getValue(),
              argType,
              environment.getGraphQlContext(),
              environment.getLocale());
      variables.put(entry.getValue(), value);
      arguments.add(
          Argument.newArgument()
              .name(entry.getKey())
              .value(VariableReference.of(entry.getValue()))
              .build());
      variableDefinitions.add(
          VariableDefinition.newVariableDefinition()
              .name(entry.getValue())
              .type(fieldDefinition.getType())
              .build());
    }

    Field.Builder fieldBuilder =
        Field.newField(resolve.getQuery())
            .alias(field.getAlias())
            .arguments(field.getArguments())
            .directives(field.getDirectives())
            .additionalData(field.getAdditionalData())
            .sourceLocation(field.getSourceLocation())
            .selectionSet(field.getSelectionSet())
            .comments(field.getComments())
            .arguments(arguments)
            .ignoredChars(field.getIgnoredChars());

    field = fieldBuilder.build();

    DataFetchingEnvironmentImpl.Builder builder =
        DataFetchingEnvironmentImpl.newDataFetchingEnvironment(environment)
            .operationDefinition(
                OperationDefinition.newOperationDefinition()
                    .name(resolve.getQuery())
                    .variableDefinitions(variableDefinitions)
                    .build())
            .parentType(objectType)
            .variables(variables)
            .fieldDefinition(objectType.getFieldDefinition(resolve.getQuery()))
            .mergedField(MergedField.newMergedField(field).build());
    return builder.build();
  }

  private DataFetchingEnvironment newOverridingEnvironment(DataFetchingEnvironment environment) {
    DataFetchingEnvironmentImpl.Builder builder =
        DataFetchingEnvironmentImpl.newDataFetchingEnvironment(environment);

    Field originalField = environment.getField();
    GraphQLType outputType = environment.getParentType();

    Field newField = (Field) transformSelection(originalField, outputType);

    builder.mergedField(MergedField.newMergedField(newField).build());
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

  private Selection<?> transformSelection(Selection<?> selection, GraphQLType outputType) {
    if (selection instanceof Field field) {
      GraphQLObjectType objectType = (GraphQLObjectType) outputType;
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
        if (overrideField.isNameChanged()) {
          fieldBuilder
              .name(overrideField.getMapping())
              .alias(overrideField.getAlias(field.getAlias()));
        }
        fieldBuilder.arguments(transformArguments(field.getArguments(), overrideField));
        if (overrideField.isTypeChanged()) {
          fieldType = GraphQLTypeUtils.toOutputType(overrideField.getOriginalType());
        }
      } else {
        fieldBuilder.arguments(transformArguments(field.getArguments()));
      }

      // 如果有子节点，则递归处理
      if (field.getSelectionSet() != null && GraphQLTypeUtils.isObjectType(fieldType)) {
        fieldBuilder.selectionSet(transformSelectionSet(field.getSelectionSet(), fieldType));
      }

      return fieldBuilder.build();
    }

    return selection;
  }

  private List<Argument> transformArguments(List<Argument> arguments) {
    return transformArguments(arguments, null);
  }

  private List<Argument> transformArguments(
      List<Argument> arguments, SchemaOverrideField overrideField) {
    if (arguments == null || arguments.isEmpty()) {
      return arguments;
    }
    List<Argument> newArguments =
        arguments.stream()
            .map(
                arg -> {
                  if (overrideField == null) {
                    return arg;
                  }
                  if (!overrideField.hasIncludeArgument(arg.getName())) {
                    return arg;
                  }
                  SchemaOverrideFieldArgument overrideArgument =
                      overrideField.getArgument(arg.getName());
                  return Argument.newArgument()
                      .name(overrideArgument.getMapping())
                      .value(arg.getValue())
                      .sourceLocation(arg.getSourceLocation())
                      .comments(arg.getComments())
                      .additionalData(arg.getAdditionalData())
                      .ignoredChars(arg.getIgnoredChars())
                      .build();
                })
            .collect(Collectors.toCollection(ArrayList::new));

    // 添加默认值
    if (overrideField != null) {
      newArguments.addAll(
          overrideField.getArguments().values().stream()
              .filter(SchemaOverrideFieldArgument::isFixedValue)
              .map(
                  item ->
                      Argument.newArgument()
                          .name(item.getName())
                          .value(item.getGraphQLValue())
                          .build())
              .toList());
    }

    return newArguments;
  }
}
