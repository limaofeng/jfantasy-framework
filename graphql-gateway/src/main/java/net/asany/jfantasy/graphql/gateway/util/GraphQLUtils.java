package net.asany.jfantasy.graphql.gateway.util;

import graphql.GraphQLContext;
import graphql.language.*;
import graphql.schema.*;
import graphql.schema.idl.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.graphql.gateway.config.*;
import net.asany.jfantasy.graphql.gateway.data.GatewayDataFetcherFactory;
import net.asany.jfantasy.graphql.gateway.data.OverrideDataFetcher;
import net.asany.jfantasy.graphql.gateway.directive.DirectiveFactory;
import net.asany.jfantasy.graphql.gateway.directive.DirectiveProcessor;
import net.asany.jfantasy.graphql.gateway.service.GraphQLInterfaceTypeResolver;
import net.asany.jfantasy.graphql.gateway.service.GraphQLUnionTypeResolver;

/**
 * GraphQL 工具类
 *
 * @author limaofeng
 */
@Slf4j
public class GraphQLUtils {

  public static Map<String, Object> buildVariables(
      DataFetchingEnvironment environment, GraphQLSchema schema) {
    List<VariableDefinition> variableDefinitions =
        environment.getOperationDefinition().getVariableDefinitions();
    Map<String, Object> variables = new HashMap<>(environment.getVariables());

    for (VariableDefinition definition : variableDefinitions) {
      String varName = definition.getName();
      if (!variables.containsKey(varName)) {
        continue;
      }

      Object fieldValue = variables.get(varName);

      if (fieldValue == null) {
        continue;
      }

      Object newFieldValue =
          processVariable(
              varName, fieldValue, GraphQLTypeUtils.convert(definition.getType(), schema));

      variables.put(varName, newFieldValue);
    }
    return variables;
  }

  public static String buildGraphQLQuery(DataFetchingEnvironment environment) {
    BuildGraphQLQueryContext context = new BuildGraphQLQueryContext(environment);

    Field field = environment.getField();

    GraphQLType type = environment.getParentType();

    OperationDefinition operationDefinition = environment.getOperationDefinition();

    OperationDefinition.Operation operation = operationDefinition.getOperation();

    StringBuilder queryBuilder = new StringBuilder();

    // 构建基本查询结构
    if (operation == OperationDefinition.Operation.QUERY) {
      queryBuilder.append("query ");
    } else if (operation == OperationDefinition.Operation.MUTATION) {
      queryBuilder.append("mutation ");
    } else if (operation == OperationDefinition.Operation.SUBSCRIPTION) {
      queryBuilder.append("subscription ");
    }

    if (operationDefinition.getName() != null) {
      queryBuilder.append(operationDefinition.getName());
    }

    StringBuilder queryBodyBuilder = processSelection(type, field, context);

    if (!context.getUsingVariables().isEmpty()) {
      processVariables(queryBuilder, context);
      queryBuilder.append(" { ").append(queryBodyBuilder).append(" }");
    } else {
      queryBuilder.append(" { ").append(queryBodyBuilder).append(" }");
    }

    if (!context.getUsingFragments().isEmpty()) {
      processFragments(queryBuilder, context);
    }

    return queryBuilder.toString();
  }

  private static Object processVariable(String varName, Object value, GraphQLType type) {
    GraphQLType varType = GraphQLTypeUtils.getSourceType(type);

    if (varType == null) {
      return value;
    }

    if (GraphQLTypeUtils.isListType(type)) {
      List<Object> values = (List<Object>) value;
      values.replaceAll(o -> processVariable(varName, o, varType));
      return values;
    }

    if (varType instanceof GraphQLScalarType scalarType) {
      // 处理标量类型的变量
      return processScalarVariable(varName, value, scalarType);
    }

    if (varType instanceof GraphQLInputObjectType) {
      // 处理输入对象类型的变量
      Map<String, Object> values = (Map<String, Object>) value;
      //noinspection PatternVariableCanBeUsed
      GraphQLInputObjectType inputObjectType = (GraphQLInputObjectType) varType;
      for (GraphQLInputObjectField field : inputObjectType.getFields()) {
        if (!values.containsKey(field.getName())) {
          continue;
        }
        Object fieldValue = values.get(field.getName());
        if (fieldValue == null) {
          continue;
        }
        processVariable(field.getName(), fieldValue, field.getType());
      }
    }

    return value;
  }

  private static Object processScalarVariable(
      String varName, Object value, GraphQLScalarType scalarType) {
    log.debug("处理变量: {} 类型: {} 值: {}", varName, scalarType.getName(), value);
    Coercing<?, ?> coercing = scalarType.getCoercing();
    GraphQLContext context = GraphQLContext.newContext().build();
    return coercing.serialize(value, context, Locale.getDefault());
  }

  private static void processVariables(
      StringBuilder queryBuilder, BuildGraphQLQueryContext context) {
    Set<String> usingVariables = context.getUsingVariables();
    Map<String, VariableDefinition> variables =
        context.getVariableDefinitions().stream()
            .collect(Collectors.toMap(VariableDefinition::getName, fragment -> fragment));
    queryBuilder.append("(");
    for (String variableName : usingVariables) {
      VariableDefinition variableDefinition = variables.get(variableName);
      queryBuilder.append("$").append(variableDefinition.getName()).append(": ");
      queryBuilder.append(formatType(variableDefinition.getType()));
      queryBuilder.append(", ");
    }
    queryBuilder.setLength(queryBuilder.length() - 2);
    queryBuilder.append(")");
  }

  private static void processFragments(
      StringBuilder queryBuilder, BuildGraphQLQueryContext context) {
    Set<String> usingFragments = context.getUsingFragments();
    Map<String, FragmentDefinition> fragments =
        context.getDefinitionsOfType(FragmentDefinition.class).stream()
            .collect(Collectors.toMap(FragmentDefinition::getName, fragment -> fragment));
    for (String fragmentName : usingFragments) {
      FragmentDefinition fragmentDefinition = fragments.get(fragmentName);
      TypeName fragmentTypeName = fragmentDefinition.getTypeCondition();
      GraphQLType fragmentType = context.getType(fragmentTypeName.getName());
      queryBuilder
          .append("fragment ")
          .append(fragmentName)
          .append(" on ")
          .append(fragmentTypeName.getName());
      processSelectionSet(
          fragmentType, fragmentDefinition.getSelectionSet(), queryBuilder, context, true);
    }
  }

  private static void buildFieldQueryPart(
      GraphQLType type, Field field, StringBuilder queryBuilder, BuildGraphQLQueryContext context) {
    if (field.getAlias() != null) {
      queryBuilder.append(field.getAlias()).append(": ");
    }
    queryBuilder.append(field.getName());
    // 添加字段参数（如果有）
    processFieldArguments(type, field, queryBuilder, context);
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
      GraphQLType type,
      SelectionSet selectionSet,
      StringBuilder queryBuilder,
      BuildGraphQLQueryContext context,
      boolean isFragment) {
    if (selectionSet != null && !selectionSet.getSelections().isEmpty()) {
      queryBuilder.append(" { ");
      selectionSet
          .getSelections()
          .forEach(
              selection -> processSelection(type, selection, queryBuilder, context, isFragment));
      queryBuilder.append(" }");
    }
  }

  private static void processFieldArguments(
      GraphQLType type, Field field, StringBuilder queryBuilder, BuildGraphQLQueryContext context) {
    if (!field.getArguments().isEmpty()) {
      queryBuilder.append("(");
      field
          .getArguments()
          .forEach(
              arg -> {
                queryBuilder.append(arg.getName()).append(": ");
                queryBuilder.append(formatValue(arg.getValue(), context)).append(", ");
              });
      queryBuilder.setLength(queryBuilder.length() - 2);
      queryBuilder.append(")");
    }
  }

  private static StringBuilder processSelection(
      GraphQLType type, Selection<?> selection, BuildGraphQLQueryContext context) {
    StringBuilder queryBuilder = new StringBuilder();
    processSelection(type, selection, queryBuilder, context, false);
    return queryBuilder;
  }

  private static void processSelection(
      GraphQLType type,
      Selection<?> selection,
      StringBuilder queryBuilder,
      BuildGraphQLQueryContext context,
      boolean isFragment) {
    if (selection instanceof Field subField) {
      buildFieldQueryPart(type, subField, queryBuilder, context);

      if (subField.getSelectionSet() != null) {
        GraphQLType fileType = GraphQLTypeUtils.getFieldType(type, subField.getName());
        processSelectionSet(
            fileType, subField.getSelectionSet(), queryBuilder, context, isFragment);
      }

      queryBuilder.append(" ");
    } else if (selection instanceof FragmentSpread fragmentSpread) {
      context.addUsingFragment(fragmentSpread.getName());
      queryBuilder.append("...").append(fragmentSpread.getName()).append(" ");
      if (!isFragment) {
        List<FragmentSpread> allFragments =
            fetchAllFragments(fragmentSpread, context.getFragmentsByName());
        allFragments.forEach(f -> context.addUsingFragment(f.getName()));
      }
    } else if (selection instanceof InlineFragment inlineFragment) {
      TypeName inlineTypeName = inlineFragment.getTypeCondition();
      queryBuilder.append("... on ").append(inlineTypeName.getName()).append(" ");
      GraphQLType fragmentType = context.getType(inlineTypeName.getName());
      processSelectionSet(
          fragmentType, inlineFragment.getSelectionSet(), queryBuilder, context, isFragment);
    } else {
      throw new RuntimeException("未知的选择类型: " + selection);
    }
  }

  public static List<FragmentSpread> fetchAllFragments(
      FragmentSpread fragment, Map<String, FragmentDefinition> allFragments) {
    List<FragmentSpread> result = new ArrayList<>();

    // 递归查找所有后代 FragmentSpread
    findDescendantsRecursive(
        result, allFragments.get(fragment.getName()).getSelectionSet(), allFragments);

    return result;
  }

  private static void findDescendantsRecursive(
      List<FragmentSpread> result,
      SelectionSet selectionSet,
      Map<String, FragmentDefinition> allFragments) {

    //noinspection rawtypes
    List<Selection> selections = selectionSet.getSelections();
    for (Selection<?> selection : selections) {
      if (selection instanceof Field subField) {
        if (subField.getSelectionSet() != null) {
          findDescendantsRecursive(result, subField.getSelectionSet(), allFragments);
        }
      } else if (selection instanceof FragmentSpread fragmentSpread) {
        result.add(fragmentSpread);
        findDescendantsRecursive(
            result, allFragments.get(fragmentSpread.getName()).getSelectionSet(), allFragments);
      }
    }
  }

  private static String formatValue(Value<?> value, BuildGraphQLQueryContext context) {
    if (value instanceof ArrayValue arrayValue) {
      return "["
          + arrayValue.getValues().stream()
              .map(val -> formatValue(val, context))
              .collect(Collectors.joining(", "))
          + "]";
    } else if (value instanceof StringValue) {
      return "\"" + ((StringValue) value).getValue() + "\"";
    } else if (value instanceof EnumValue) {
      return ((EnumValue) value).getName();
    } else if (value instanceof ObjectValue objectValue) {
      return "{"
          + objectValue.getObjectFields().stream()
              .map(val -> formatObjectField(val, context))
              .collect(Collectors.joining(", "))
          + "}";
    } else if (value instanceof IntValue) {
      return ((IntValue) value).getValue().toString();
    } else if (value instanceof FloatValue) {
      return ((FloatValue) value).getValue().toString();
    } else if (value instanceof VariableReference variableReference) {
      context.addUsingVariable(variableReference.getName());
      return "$" + variableReference.getName();
    } else if (value instanceof BooleanValue booleanValue) {
      return booleanValue.isValue() ? "true" : "false";
    } else if (value instanceof NullValue) {
      return "null";
    }
    return value.toString();
  }

  private static String formatObjectField(ObjectField field, BuildGraphQLQueryContext context) {
    return field.getName() + ": " + formatValue(field.getValue(), context);
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

      // 处理枚举类型
      List<EnumTypeDefinition> enumTypes = schemaRegistry.getTypes(EnumTypeDefinition.class);
      for (EnumTypeDefinition enumType : enumTypes) {
        TypeName typeName = TypeName.newTypeName(enumType.getName()).build();
        if (!mergedRegistry.hasType(typeName)) {
          continue;
        }
        //noinspection OptionalGetWithoutIsPresent
        EnumTypeDefinition extendEnum =
            schemaRegistry.getType(typeName, EnumTypeDefinition.class).get();
        schemaRegistry.remove(extendEnum);
      }

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
                .typeResolver(new GraphQLInterfaceTypeResolver()));
      }

      // 处理联合类型
      List<UnionTypeDefinition> unionTypes = schemaRegistry.getTypes(UnionTypeDefinition.class);
      for (UnionTypeDefinition unionType : unionTypes) {
        runtimeWiringBuilder.type(
            TypeRuntimeWiring.newTypeWiring(unionType.getName())
                .typeResolver(new GraphQLUnionTypeResolver()));
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
      if (objectType == null) {
        continue;
      }
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
    for (FieldDefinition fieldDefinition : objectTypeDefinition.getFieldDefinitions()) {
      extensionBuilder.fieldDefinition(fieldDefinition);
    }
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

  private static class BuildGraphQLQueryContext {
    private final DataFetchingEnvironment environment;
    @Getter private final Set<String> usingFragments = new LinkedHashSet<>();
    @Getter private final Set<String> usingVariables = new LinkedHashSet<>();

    public BuildGraphQLQueryContext(DataFetchingEnvironment environment) {
      this.environment = environment;
    }

    public void addUsingFragment(String name) {
      usingFragments.add(name);
    }

    public void addUsingVariable(String name) {
      usingVariables.add(name);
    }

    public List<VariableDefinition> getVariableDefinitions() {
      return this.environment.getOperationDefinition().getVariableDefinitions();
    }

    public <T extends Definition<T>> List<T> getDefinitionsOfType(Class<T> definitionClass) {
      return this.environment.getDocument().getDefinitionsOfType(definitionClass);
    }

    public GraphQLType getType(String name) {
      return this.environment.getGraphQLSchema().getType(name);
    }

    public Map<String, FragmentDefinition> getFragmentsByName() {
      return this.environment.getFragmentsByName();
    }
  }

  /**
   * Converts an HTTP URL to a WebSocket URL for subscriptions, allowing a custom subscription path.
   *
   * @param httpUrl the HTTP URL to convert.
   * @param subscriptionPath the path to append for WebSocket subscriptions.
   * @return the WebSocket URL.
   */
  public static String convertToWebSocketUrl(String httpUrl, String subscriptionPath) {
    if (httpUrl == null || httpUrl.isEmpty()) {
      throw new IllegalArgumentException("The URL cannot be null or empty");
    }
    if (subscriptionPath == null || subscriptionPath.isEmpty()) {
      throw new IllegalArgumentException("Subscription path cannot be null or empty");
    }

    // Ensure the URL starts with http or https
    if (!httpUrl.startsWith("http://") && !httpUrl.startsWith("https://")) {
      throw new IllegalArgumentException("The URL must start with http:// or https://");
    }

    // Replace http with ws and https with wss
    String wsUrl = httpUrl.replace("http://", "ws://").replace("https://", "wss://");

    // Ensure the subscription path starts with a "/"
    if (!subscriptionPath.startsWith("/")) {
      subscriptionPath = "/" + subscriptionPath;
    }

    int pathStartIndex = wsUrl.indexOf("/", wsUrl.indexOf("//") + 2);
    int pathEndIndex = wsUrl.indexOf("?", pathStartIndex);
    pathEndIndex = (pathEndIndex == -1) ? wsUrl.length() : pathEndIndex;

    if (pathStartIndex != -1) {
      wsUrl = wsUrl.substring(0, pathStartIndex) + subscriptionPath + wsUrl.substring(pathEndIndex);
    }

    return wsUrl; // Return the original URL if no path is found
  }
}
