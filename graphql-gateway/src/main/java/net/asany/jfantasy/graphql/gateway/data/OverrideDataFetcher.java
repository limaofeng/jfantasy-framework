/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.graphql.gateway.data;

import graphql.execution.MergedField;
import graphql.execution.directives.QueryDirectives;
import graphql.language.*;
import graphql.schema.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.config.FieldResolve;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverride;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverrideField;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverrideFieldArgument;
import net.asany.jfantasy.graphql.gateway.directive.DirectiveProcessor;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;
import net.asany.jfantasy.graphql.gateway.util.GraphQLValueUtils;

@Slf4j
@Builder(builderClassName = "Builder")
public class OverrideDataFetcher implements DataFetcher<Object> {

  private final DirectiveProcessor directiveProcessor;
  private final SchemaOverride override;
  private final SchemaOverrideField overrideField;
  private final DataFetcher<?> dataFetcher;
  private final FieldResolve resolve;

  @Override
  public Object get(DataFetchingEnvironment environment) throws Exception {
    if (resolve != null) {
      return dataFetcher.get(newFileDelegationEnvironment(environment));
    }
    DataFetcher<?> newDataFetcher = directiveProcessor.process(environment, dataFetcher);
    return newDataFetcher.get(newOverridingEnvironment(environment));
  }

  private DataFetchingEnvironment newFileDelegationEnvironment(
      DataFetchingEnvironment environment) {
    Field field = environment.getField();

    GraphQLSchema graphQLSchema = environment.getGraphQLSchema();
    GraphQLObjectType objectType = graphQLSchema.getQueryType();

    List<VariableDefinition> variableDefinitions = new ArrayList<>();
    Map<String, Object> variables = new HashMap<>();
    List<Argument> arguments = new ArrayList<>();

    for (Map.Entry<String, FieldResolve.FieldArgument> entry : resolve.getArguments().entrySet()) {
      FieldResolve.FieldArgument argValue = entry.getValue();
      GraphQLObjectType fieldType = (GraphQLObjectType) environment.getFieldType();
      FieldDefinition fieldDefinition = fieldType.getField(entry.getKey()).getDefinition();
      assert fieldDefinition != null;
      GraphQLOutputType argType =
          (GraphQLOutputType)
              graphQLSchema.getType(GraphQLTypeUtils.getTypeName(argValue.getType()).getName());

      if (argValue.isReference()) {
        VariableReference variableReference = argValue.getValue(VariableReference.class);
        Object value =
            GraphQLValueUtils.convert(
                environment.getSource(),
                variableReference.getName(),
                argType,
                environment.getGraphQlContext(),
                environment.getLocale());
        variables.put(variableReference.getName(), value);
        variableDefinitions.add(
            VariableDefinition.newVariableDefinition()
                .name(variableReference.getName())
                .type(fieldDefinition.getType())
                .build());
      }

      arguments.add(Argument.newArgument().name(entry.getKey()).value(argValue.getValue()).build());
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

    MergedField mergedField = MergedField.newMergedField(field).build();

    DataFetchingEnvironmentImpl.Builder builder =
        DataFetchingEnvironmentImpl.newDataFetchingEnvironment(environment)
            .operationDefinition(
                OperationDefinition.newOperationDefinition()
                    .name(resolve.getQuery())
                    .operation(OperationDefinition.Operation.QUERY)
                    .variableDefinitions(variableDefinitions)
                    .build())
            .parentType(objectType)
            .variables(variables)
            .fieldDefinition(objectType.getFieldDefinition(resolve.getQuery()))
            .mergedField(mergedField)
            .queryDirectives(
                QueryDirectives.newQueryDirectives()
                    .field(field)
                    .mergedField(mergedField)
                    .schema(graphQLSchema)
                    .build());
    return builder.build();
  }

  private DataFetchingEnvironment newOverridingEnvironment(DataFetchingEnvironment environment) {
    DataFetchingEnvironmentImpl.Builder builder =
        DataFetchingEnvironmentImpl.newDataFetchingEnvironment(environment);

    Field originalField = environment.getField();
    GraphQLObjectType outputType = (GraphQLObjectType) environment.getParentType();

    Field newField = (Field) transformSelection(originalField, outputType);

    builder.mergedField(MergedField.newMergedField(newField).build());

    if (dataFetcher instanceof ServiceDataFetcher serviceDataFetcher) {
      builder
          .graphQLSchema(serviceDataFetcher.getSchema())
          .parentType(serviceDataFetcher.getSchema().getType(outputType.getName()));
    }

    return builder.build();
  }

  private SelectionSet transformSelectionSet(
      SelectionSet originalSelectionSet, GraphQLOutputType outputType) {
    GraphQLType fieldType = GraphQLTypeUtils.getSourceType(outputType);
    return SelectionSet.newSelectionSet()
        .selections(
            originalSelectionSet.getSelections().stream()
                .map(item -> transformSelection(item, fieldType))
                .toList())
        .additionalData(originalSelectionSet.getAdditionalData())
        .sourceLocation(originalSelectionSet.getSourceLocation())
        .comments(originalSelectionSet.getComments())
        .ignoredChars(originalSelectionSet.getIgnoredChars())
        .build();
  }

  private Selection<?> transformSelection(Selection<?> selection, GraphQLType outputType) {
    if (selection instanceof Field field) {

      if (!(outputType instanceof GraphQLFieldsContainer fieldsContainer)) {
        log.warn("Output type {} is not a fields container", outputType.toString());
        return selection;
      }

      GraphQLFieldDefinition fieldDefinition = fieldsContainer.getFieldDefinition(field.getName());

      if (fieldDefinition == null) {
        log.warn("Field {} not found in type {}", field.getName(), fieldsContainer.getName());
        return selection;
      }

      GraphQLOutputType fieldType = fieldDefinition.getType();

      Field.Builder fieldBuilder =
          Field.newField(field.getName())
              .alias(field.getAlias())
              .arguments(field.getArguments())
              .directives(field.getDirectives());

      // 如果有覆盖配置，则使用覆盖配置
      if (override.hasIncludeField(fieldsContainer.getName(), field.getName())) {
        SchemaOverrideField overrideField =
            override.getField(fieldsContainer.getName(), field.getName());
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
      if (field.getSelectionSet() != null) {
        if (GraphQLTypeUtils.isFieldsContainerType(fieldType)) {
          fieldBuilder.selectionSet(transformSelectionSet(field.getSelectionSet(), fieldType));
        } else {
          log.warn("Field {} is not a fields container", field.getName());
        }
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
