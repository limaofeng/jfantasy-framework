package net.asany.jfantasy.graphql.gateway.util;

import graphql.language.*;
import graphql.schema.*;
import graphql.schema.idl.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.graphql.gateway.config.*;
import net.asany.jfantasy.graphql.gateway.data.GatewayDataFetcherFactory;
import net.asany.jfantasy.graphql.gateway.data.OverrideDataFetcher;
import net.asany.jfantasy.graphql.gateway.directive.DirectiveFactory;
import net.asany.jfantasy.graphql.gateway.directive.DirectiveProcessor;
import net.asany.jfantasy.graphql.gateway.service.GraphQLServiceTypeResolver;

/**
 * GraphQL 工具类
 *
 * @author limaofeng
 */
@Slf4j
public class GraphQLUtils {

  public static String buildGraphQLQuery(DataFetchingEnvironment environment) {
    Field field = environment.getField();

    GraphQLType type = environment.getParentType();

    OperationDefinition operation = environment.getOperationDefinition();

    StringBuilder queryBuilder = new StringBuilder();

    // 构建基本查询结构
    queryBuilder.append("query ");

    if (operation.getName() != null) {
      queryBuilder.append(operation.getName());
    }

    if (!operation.getVariableDefinitions().isEmpty()) {
      queryBuilder.append("(");
      for (VariableDefinition variableDefinition : operation.getVariableDefinitions()) {
        queryBuilder.append("$").append(variableDefinition.getName()).append(": ");
        queryBuilder.append(formatType(variableDefinition.getType()));
        queryBuilder.append(", ");
      }
      queryBuilder.setLength(queryBuilder.length() - 2);
      queryBuilder.append(")");
    }

    queryBuilder.append(" { ");

    processSelection(type, field, queryBuilder);

    queryBuilder.append(" }");
    return queryBuilder.toString();
  }

  private static void buildFieldQueryPart(
      GraphQLType type, Field field, StringBuilder queryBuilder) {
    if (field.getAlias() != null) {
      queryBuilder.append(field.getAlias()).append(": ");
    }
    queryBuilder.append(field.getName());
    // 添加字段参数（如果有）
    processFieldArguments(type, field, queryBuilder);
  }

  private static String formatType(Type<?> type) {
    if (type instanceof NonNullType) {
      return formatType(((NonNullType) type).getType()) + "!";
    } else if (type instanceof ListType) {
      return "[" + formatType(((ListType) type).getType()) + "]";
    } else if (type instanceof TypeName) {
      return ((TypeName) type).getName();
    }
    throw new RuntimeException("未知的类型: " + type);
  }

  private static void processSelectionSet(
      GraphQLType type, SelectionSet selectionSet, StringBuilder queryBuilder) {
    if (selectionSet != null && !selectionSet.getSelections().isEmpty()) {
      queryBuilder.append(" { ");
      selectionSet
          .getSelections()
          .forEach(selection -> processSelection(type, selection, queryBuilder));
      queryBuilder.append(" }");
    }
  }

  private static void processFieldArguments(
      GraphQLType type, Field field, StringBuilder queryBuilder) {
    if (!field.getArguments().isEmpty()) {
      queryBuilder.append("(");
      field
          .getArguments()
          .forEach(
              arg -> {
                queryBuilder.append(arg.getName()).append(": ");
                queryBuilder.append(formatValue(arg.getValue())).append(", ");
              });
      queryBuilder.setLength(queryBuilder.length() - 2);
      queryBuilder.append(")");
    }
  }

  private static void processSelection(
      GraphQLType type, Selection<?> selection, StringBuilder queryBuilder) {
    if (selection instanceof Field subField) {
      buildFieldQueryPart(type, subField, queryBuilder);

      if (subField.getSelectionSet() != null) {
        GraphQLType fileType = GraphQLTypeUtils.getFieldType(type, subField.getName());
        processSelectionSet(fileType, subField.getSelectionSet(), queryBuilder);
      }

      queryBuilder.append(" ");
    }
    // 这里可以添加对其他类型的 Selection 的处理，如 FragmentSpread 或 InlineFragment
  }

  private static String formatValue(Value<?> value) {
    if (value instanceof ArrayValue arrayValue) {
      return "["
          + arrayValue.getValues().stream()
              .map(GraphQLUtils::formatValue)
              .collect(Collectors.joining(", "))
          + "]";
    } else if (value instanceof StringValue) {
      return "\"" + ((StringValue) value).getValue() + "\"";
    } else if (value instanceof EnumValue) {
      return ((EnumValue) value).getName();
    } else if (value instanceof ObjectValue objectValue) {
      return "{"
          + objectValue.getObjectFields().stream()
              .map(GraphQLUtils::formatObjectField)
              .collect(Collectors.joining(", "))
          + "}";
    } else if (value instanceof IntValue) {
      return ((IntValue) value).getValue().toString();
    } else if (value instanceof FloatValue) {
      return ((FloatValue) value).getValue().toString();
    } else if (value instanceof VariableReference variableReference) {
      return "$" + variableReference.getName();
    } else if (value instanceof BooleanValue booleanValue) {
      return booleanValue.isValue() ? "true" : "false";
    } else if (value instanceof NullValue) {
      return "null";
    }
    return value.toString();
  }

  private static String formatObjectField(ObjectField field) {
    return field.getName() + ": " + formatValue(field.getValue());
  }

  public static GraphQLSchema buildSchema(String sdl) {
    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeRegistry = schemaParser.parse(sdl);

    RuntimeWiring runtimeWiring = buildWiring();
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
  }

  private static RuntimeWiring buildWiring() {
    // 在这里配置您的数据获取器（data fetchers）和类型解析器（type resolvers）
    return RuntimeWiring.newRuntimeWiring()
        .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
        //        .scalar(
        //            GraphQLScalarType.newScalar()
        //                .name("OrderBy")
        //                .description("排序对象, 格式如：createdAt_ASC ")
        //                //                .coercing(new OrderCoercing())
        //                .build())
        //        .scalar(
        //            GraphQLScalarType.newScalar()
        //                .name("File")
        //                .coercing(ExtendedScalars.GraphQLShort.getCoercing())
        //                .build())
        .build();
  }

  public static GraphQLSchema mergeSchemas(
      List<GraphQLSchema> schemas,
      SchemaOverride override,
      GatewayDataFetcherFactory dataFetcherFactory,
      DirectiveFactory directiveFactory) {

    DirectiveProcessor directiveProcessor =
        DirectiveProcessor.builder().factory(directiveFactory).build();

    // 使用SchemaPrinter获取现有模式的SDL表示
    SchemaPrinter schemaPrinter =
        new SchemaPrinter(
            SchemaPrinter.Options.defaultOptions()
                .includeScalarTypes(true) // 根据需要包括标量类型
                .includeSchemaDefinition(false) // 包括模式定义
                .includeDirectives(true) // 包括指令
            );

    // 解析现有模式的SDL表示
    SchemaParser schemaParser = new SchemaParser();

    GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();
    codeRegistryBuilder.defaultDataFetcher(dataFetcherFactory);

    // 创建 TypeDefinitionRegistry 并添加现有模式和扩展定义
    TypeDefinitionRegistry mergedRegistry = new TypeDefinitionRegistry();

    RuntimeWiring.Builder runtimeWiringBuilder = RuntimeWiring.newRuntimeWiring();

    Map<FieldCoordinates, DataFetcher<?>> dataFetchers = new HashMap<>();
    Map<FieldCoordinates, SchemaOverrideField> resolves = new HashMap<>();

    for (GraphQLSchema schema : schemas) {
      TypeDefinitionRegistry schemaRegistry = schemaParser.parse(schemaPrinter.print(schema));

      // 处理根类型 Query, Mutation, Subscription
      mergeRootTypes(mergedRegistry, schemaRegistry);

      // 处理类型扩展
      mergeIgnoreTypes(mergedRegistry, schemaRegistry, "PageInfo");

      // 处理标量类型
      List<ScalarTypeDefinition> scalarTypes = mergeScalarTypes(mergedRegistry, schemaRegistry);
      for (ScalarTypeDefinition scalarType : scalarTypes) {
        runtimeWiringBuilder.scalar((GraphQLScalarType) schema.getType(scalarType.getName()));
      }

      // 处理指令类型
      mergeDirectiveTypes(mergedRegistry, schemaRegistry);

      // 处理接口类型
      List<InterfaceTypeDefinition> interfaceTypes =
          schemaRegistry.getTypes(InterfaceTypeDefinition.class);
      for (InterfaceTypeDefinition interfaceType : interfaceTypes) {
        runtimeWiringBuilder.type(
            TypeRuntimeWiring.newTypeWiring(interfaceType.getName())
                .typeResolver(new GraphQLServiceTypeResolver(null)));
      }

      // 处理联合类型
      List<UnionTypeDefinition> unionTypes = schemaRegistry.getTypes(UnionTypeDefinition.class);
      for (UnionTypeDefinition unionType : unionTypes) {
        runtimeWiringBuilder.type(
            TypeRuntimeWiring.newTypeWiring(unionType.getName())
                .typeResolver(new GraphQLServiceTypeResolver(null)));
      }

      mergedRegistry.merge(schemaRegistry);

      // 处理数据获取器
      dataFetchers.putAll(mergeDataFetchers(schema));
    }

    // 处理覆盖配置
    for (SchemaOverrideType overrideType : override.getTypes()) {
      Optional<ObjectTypeDefinition> typeDefinitionOptional =
          mergedRegistry.getType(overrideType.getType(), ObjectTypeDefinition.class);
      if (typeDefinitionOptional.isEmpty()) {
        continue;
      }
      ObjectTypeDefinition typeDefinition = typeDefinitionOptional.get();
      mergedRegistry.remove(typeDefinition);

      Set<String> excludeFields =
          overrideType.getFields().entrySet().stream()
              .filter(entry -> entry.getValue().isExclude())
              .map(Map.Entry::getKey)
              .collect(Collectors.toSet());

      ObjectTypeDefinition.Builder newTypeDefinitionBuilder =
          ObjectTypeDefinition.newObjectTypeDefinition()
              .name(typeDefinition.getName())
              .description(typeDefinition.getDescription())
              .implementz(typeDefinition.getImplements())
              .directives(typeDefinition.getDirectives());

      Map<String, FieldDefinition> fieldMap =
          typeDefinition.getFieldDefinitions().stream()
              .collect(Collectors.toMap(FieldDefinition::getName, item -> item));

      for (Map.Entry<String, SchemaOverrideField> entry : overrideType.getFields().entrySet()) {
        String fieldName = entry.getKey();
        SchemaOverrideField config = entry.getValue();
        if (config.isExclude()) {
          continue;
        }
        FieldDefinition fieldDefinition = fieldMap.get(config.getMapping());
        if (fieldDefinition == null) {
          log.warn("Field {} not found in type {}", fieldName, typeDefinition.getName());
          continue;
        }
        if (config.isNameChanged()) {
          excludeFields.add(fieldDefinition.getName());
        }
        FieldDefinition.Builder fieldBuilder =
            FieldDefinition.newFieldDefinition()
                .name(fieldDefinition.getName())
                .type(fieldDefinition.getType())
                .sourceLocation(fieldDefinition.getSourceLocation())
                .additionalData(fieldDefinition.getAdditionalData())
                .ignoredChars(fieldDefinition.getIgnoredChars())
                .inputValueDefinitions(fieldDefinition.getInputValueDefinitions())
                .directives(fieldDefinition.getDirectives());

        // 处理字段名称
        if (config.isNameChanged()) {
          fieldBuilder.name(fieldName);
        }

        // 处理字段类型
        if (config.isTypeChanged(fieldDefinition.getType())) {
          if (!mergedRegistry.hasType(GraphQLTypeUtils.getTypeName(config.getType()))) {
            log.warn("Type {} not found in type {}", config.getType(), typeDefinition.getName());
          } else {
            config.setOriginalType(fieldDefinition.getType());
            fieldBuilder.type(config.getType());
          }
        }

        if (config.getArguments() != null) {
          Map<String, InputValueDefinition> inputValueMap =
              fieldDefinition.getInputValueDefinitions().stream()
                  .collect(Collectors.toMap(InputValueDefinition::getName, item -> item));

          for (Map.Entry<String, SchemaOverrideFieldArgument> argumentEntry :
              config.getArguments().entrySet()) {
            String argumentName = argumentEntry.getKey();
            SchemaOverrideFieldArgument argumentConfig = argumentEntry.getValue();
            if (argumentConfig.isExclude()) {
              inputValueMap.remove(argumentName);
              continue;
            }
            InputValueDefinition inputValueDefinition =
                inputValueMap.get(argumentConfig.getMapping());
            if (inputValueDefinition == null) {
              log.warn("Argument {} not found in field {}", argumentName, fieldName);
              continue;
            }
            InputValueDefinition.Builder inputValueBuilder =
                InputValueDefinition.newInputValueDefinition()
                    .type(inputValueDefinition.getType())
                    .name(argumentName)
                    .defaultValue(inputValueDefinition.getDefaultValue())
                    .directives(inputValueDefinition.getDirectives())
                    .description(inputValueDefinition.getDescription());
            if (argumentConfig.isNameChanged()) {
              inputValueMap.replace(
                  argumentConfig.getMapping(), inputValueBuilder.name(argumentName).build());
            }
          }
          fieldBuilder.inputValueDefinitions(inputValueMap.values().stream().toList());
        }

        FieldDefinition newFieldDefinition = fieldBuilder.build();

        FieldCoordinates coordinates =
            FieldCoordinates.coordinates(typeDefinition.getName(), fieldDefinition.getName());
        FieldCoordinates newCoordinates =
            FieldCoordinates.coordinates(typeDefinition.getName(), newFieldDefinition.getName());

        DataFetcher<?> dataFetcher = dataFetchers.remove(coordinates);
        if (config.getDataFetcher() != null) {
          dataFetcher = dataFetcherFactory.getDataFetcher(config.getDataFetcher());
        } else if (config.getResolve() != null) {
          resolves.put(newCoordinates, config);
          dataFetcher = dataFetcherFactory.getFieldResolver();
        }

        if (dataFetcher != null) {
          dataFetchers.put(newCoordinates, dataFetcher);
        }

        fieldMap.put(fieldName, newFieldDefinition);
      }

      List<FieldDefinition> fieldDefinitions =
          fieldMap.values().stream()
              .filter(item -> !excludeFields.contains(item.getName()))
              .collect(Collectors.toList());
      newTypeDefinitionBuilder.fieldDefinitions(fieldDefinitions);

      mergedRegistry.add(newTypeDefinitionBuilder.build());
    }

    // 处理数据获取器
    boolean isNoDataFetcher = override.getTypes().isEmpty();
    for (Map.Entry<FieldCoordinates, DataFetcher<?>> entry : dataFetchers.entrySet()) {
      DataFetcher<?> dataFetcher = entry.getValue();
      if (!isNoDataFetcher) {
        OverrideDataFetcher.Builder newDataFetcherBuilder =
            OverrideDataFetcher.builder()
                .directiveProcessor(directiveProcessor)
                .dataFetcher(dataFetcher)
                .override(override);

        if (resolves.containsKey(entry.getKey())) {
          SchemaOverrideField overrideField = resolves.get(entry.getKey());
          FieldResolve resolve = overrideField.getResolve();
          ObjectTypeDefinition objectType =
              (ObjectTypeDefinition)
                  mergedRegistry
                      .getType("Query")
                      .orElseThrow(() -> new RuntimeException("Query not found"));
          FieldDefinition fieldDefinition =
              ObjectUtil.find(objectType.getFieldDefinitions(), "name", resolve.getQuery());

          for (Map.Entry<String, FieldResolve.FieldArgument> argumentEntry :
              resolve.getArguments().entrySet()) {
            FieldResolve.FieldArgument fieldArgument = argumentEntry.getValue();
            InputValueDefinition inputValueDefinition =
                ObjectUtil.find(
                    fieldDefinition.getInputValueDefinitions(), "name", fieldArgument.getName());
            if (inputValueDefinition == null) {
              throw new RuntimeException(
                  "Argument "
                      + fieldArgument.getName()
                      + " not found in field "
                      + fieldDefinition.getName());
            }
            // 设置真实的参数类型
            fieldArgument.setType(inputValueDefinition.getType());
            if (fieldArgument.isReference()) {
              continue;
            }
            @SuppressWarnings("rawtypes")
            Optional<TypeDefinition> typeOptional =
                mergedRegistry.getType(inputValueDefinition.getType());
            if (typeOptional.isEmpty()) {
              throw new RuntimeException(
                  "Type "
                      + inputValueDefinition.getType()
                      + " not found in field "
                      + fieldDefinition.getName());
            }
            fieldArgument.setValue(fieldArgument.getValue());
          }

          newDataFetcherBuilder.overrideField(overrideField).resolve(resolve);
        }

        dataFetcher = newDataFetcherBuilder.build();
      }
      codeRegistryBuilder.dataFetcher(entry.getKey(), dataFetcher);
    }

    // 构建新的GraphQL模式
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    runtimeWiringBuilder.codeRegistry(codeRegistryBuilder.build());

    //    // 用于扩展现有模式的SDL表示
    //    String extensionSchemaSDL = "extend type Query { newField: String }";
    //    TypeDefinitionRegistry extensionRegistry = schemaParser.parse(extensionSchemaSDL);
    //    mergedRegistry.merge(extensionRegistry);
    //
    //    runtimeWiringBuilder.type(
    //        "Query", builder -> builder.dataFetcher("newField", new
    // StaticDataFetcher("newField")));

    RuntimeWiring runtimeWiring = runtimeWiringBuilder.build();

    ClassUtil.setFieldValue(
        mergedRegistry,
        "directiveDefinitions",
        (Map<String, DirectiveDefinition> old) -> {
          old.putAll(directiveFactory.getDirectiveDefinitions());
          return old;
        });

    return schemaGenerator.makeExecutableSchema(mergedRegistry, runtimeWiring);
  }

  private static Map<FieldCoordinates, DataFetcher<?>> mergeDataFetchers(GraphQLSchema schema) {
    Map<FieldCoordinates, DataFetcher<?>> dataFetchers = new HashMap<>();
    GraphQLObjectType[] objectTypes =
        new GraphQLObjectType[] {
          schema.getQueryType(), schema.getMutationType(), schema.getSubscriptionType()
        };
    for (GraphQLObjectType objectType : objectTypes) {
      for (GraphQLFieldDefinition field : objectType.getFields()) {
        FieldCoordinates coordinates =
            FieldCoordinates.coordinates(objectType.getName(), field.getName());
        DataFetcher<?> dataFetcher = schema.getCodeRegistry().getDataFetcher(coordinates, field);
        dataFetchers.put(coordinates, dataFetcher);
      }
    }
    return dataFetchers;
  }

  private static void mergeDirectiveTypes(
      TypeDefinitionRegistry mergedRegistry, TypeDefinitionRegistry schemaRegistry) {
    Map<String, DirectiveDefinition> directiveTypes = schemaRegistry.getDirectiveDefinitions();
    for (Map.Entry<String, DirectiveDefinition> entry : directiveTypes.entrySet()) {
      if (mergedRegistry.getDirectiveDefinitions().containsKey(entry.getKey())) {
        schemaRegistry.remove(entry.getValue());
      }
    }
  }

  private static List<ScalarTypeDefinition> mergeScalarTypes(
      TypeDefinitionRegistry mergedRegistry, TypeDefinitionRegistry schemaRegistry) {
    Map<String, ScalarTypeDefinition> scalarTypes = schemaRegistry.scalars();
    List<ScalarTypeDefinition> scalarTypeDefinitions = new ArrayList<>(scalarTypes.values());

    for (Map.Entry<String, ScalarTypeDefinition> entry : scalarTypes.entrySet()) {
      if (mergedRegistry.hasType(TypeName.newTypeName(entry.getKey()).build())) {
        schemaRegistry.remove(entry.getValue());
        scalarTypeDefinitions.remove(entry.getValue());
      }
    }

    return scalarTypeDefinitions;
  }

  private static ObjectTypeExtensionDefinition transformObjectTypeExtensionDefinition(
      ObjectTypeDefinition objectTypeDefinition) {
    ObjectTypeExtensionDefinition.Builder extensionBuilder =
        ObjectTypeExtensionDefinition.newObjectTypeExtensionDefinition();
    extensionBuilder.name(objectTypeDefinition.getName());
    // 复制字段
    objectTypeDefinition.getFieldDefinitions().forEach(extensionBuilder::fieldDefinition);
    // 复制指令（如果有）
    objectTypeDefinition.getDirectives().forEach(extensionBuilder::directive);
    return extensionBuilder.build();
  }

  private static void mergeRootType(
      String name, TypeDefinitionRegistry mergedRegistry, TypeDefinitionRegistry schemaRegistry) {
    TypeName typeName = TypeName.newTypeName(name).build();
    if (!mergedRegistry.hasType(typeName) || !schemaRegistry.hasType(typeName)) {
      return;
    }
    //noinspection OptionalGetWithoutIsPresent
    ObjectTypeDefinition extendQuery =
        schemaRegistry.getType(typeName, ObjectTypeDefinition.class).get();
    schemaRegistry.remove(extendQuery);

    // 创建一个新的 ObjectTypeExtensionDefinition Builder
    ObjectTypeExtensionDefinition objectTypeExtensionDefinition =
        transformObjectTypeExtensionDefinition(extendQuery);

    schemaRegistry.add(objectTypeExtensionDefinition);
  }

  private static void mergeIgnoreTypes(
      TypeDefinitionRegistry mergedRegistry,
      TypeDefinitionRegistry schemaRegistry,
      String... ignoreTypes) {
    for (String name : ignoreTypes) {
      TypeName typeName = TypeName.newTypeName(name).build();
      if (!mergedRegistry.hasType(typeName) || !schemaRegistry.hasType(typeName)) {
        return;
      }
      schemaRegistry.getType(typeName).ifPresent(schemaRegistry::remove);
    }
  }

  private static void mergeRootTypes(
      TypeDefinitionRegistry mergedRegistry, TypeDefinitionRegistry schemaRegistry) {
    String[] rootTypes = new String[] {"Query", "Mutation", "Subscription"};

    for (String rootType : rootTypes) {
      mergeRootType(rootType, mergedRegistry, schemaRegistry);
    }
  }
}
