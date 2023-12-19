package org.jfantasy.graphql.util;

import graphql.language.*;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import graphql.schema.idl.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jfantasy.framework.util.Stack;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.graphql.gateway.config.GraphQLServiceOverride;
import org.jfantasy.graphql.gateway.service.GraphQLService;
import org.jfantasy.graphql.gateway.service.GraphQLServiceTypeResolver;
import org.jfantasy.graphql.scalars.OrderCoercing;

/**
 * GraphQL 工具类
 *
 * @author limaofeng
 */
public class GraphQLUtils {

  public static final String ENV_CURRENT_SERVICE = "CURRENT_SERVICE";
  public static final String ENV_CURRENT_TYPE = "BUILD_QUERY_CURRENT_TYPE";

  public static String getExecutionStepInfoPath(DataFetchingEnvironment environment) {
    return environment.getExecutionStepInfo().getPath().toString();
  }

  public static boolean hasFetchFields(DataFetchingEnvironment environment, String... paths) {
    List<Field> fields = environment.getExecutionStepInfo().getField().getFields();
    String rootPath = environment.getExecutionStepInfo().getPath().toString();
    String[] rootPaths = StringUtil.tokenizeToStringArray(rootPath, "/");
    Field rootField =
        ObjectUtil.find(
            fields,
            item -> rootPaths[0].equals(item.getAlias()) || rootPaths[0].equals(item.getName()));
    for (String path : paths) {
      String[] internalPaths = StringUtil.tokenizeToStringArray(path, ".");
      assert rootField != null;
      List<Field> selections = rootField.getSelectionSet().getSelectionsOfType(Field.class);
      for (String fieldName : internalPaths) {
        Field node = ObjectUtil.find(selections, item -> fieldName.equals(item.getName()));
        if (node == null) {
          return false;
        }
        selections = node.getSelectionSet().getSelectionsOfType(Field.class);
      }
    }
    return true;
  }

  public static String buildGraphQLQuery(DataFetchingEnvironment environment) {
    GraphQLService service = environment.getGraphQlContext().get(GraphQLUtils.ENV_CURRENT_SERVICE);
    GraphQLServiceOverride override = service.getOverrideConfig();

    Field field = environment.getField();
    GraphQLType type = GraphQLTypeUtils.getSourceType(environment.getFieldType());

    Stack<GraphQLType> currentType = new Stack<>();
    environment.getGraphQlContext().put(GraphQLUtils.ENV_CURRENT_TYPE, currentType);

    GraphQLObjectType objectType = (GraphQLObjectType) environment.getParentType();
    currentType.push(objectType);

    OperationDefinition operation = environment.getOperationDefinition();

    StringBuilder queryBuilder = new StringBuilder();

    // 构建基本查询结构
    queryBuilder.append("query ");

    if (operation.getName() != null) {
      queryBuilder.append(operation.getName());
    }

    if (!operation.getVariableDefinitions().isEmpty()) {
      for (VariableDefinition variableDefinition : operation.getVariableDefinitions()) {
        queryBuilder.append("(");
        queryBuilder.append("$").append(variableDefinition.getName()).append(": ");
        queryBuilder.append(formatType(variableDefinition.getType()));
        queryBuilder.append(")");
      }
    }

    queryBuilder.append(" { ");

    buildFieldQueryPart(environment, override, objectType, field, queryBuilder);

    // 处理选择集
    currentType.push(type);
    processSelectionSet(field.getSelectionSet(), queryBuilder, environment);
    currentType.pop();

    queryBuilder.append(" }");
    return queryBuilder.toString();
  }

  private static void buildFieldQueryPart(
      DataFetchingEnvironment environment,
      GraphQLServiceOverride override,
      GraphQLObjectType objectType,
      Field field,
      StringBuilder queryBuilder) {
    if (override.isFieldRenamed(objectType.getName(), field.getName())) {
      String name = override.getOriginalFieldName(objectType.getName(), field.getName());
      queryBuilder.append(StringUtil.defaultValue(field.getAlias(), field.getName())).append(": ");
      queryBuilder.append(name);
    } else {
      if (field.getAlias() != null) {
        queryBuilder.append(field.getAlias()).append(": ");
      }
      queryBuilder.append(field.getName());
    }

    // 添加字段参数（如果有）
    processFieldArguments(field, queryBuilder, environment);
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
      SelectionSet selectionSet, StringBuilder queryBuilder, DataFetchingEnvironment environment) {
    if (selectionSet != null && !selectionSet.getSelections().isEmpty()) {
      queryBuilder.append(" { ");
      selectionSet
          .getSelections()
          .forEach(selection -> processSelection(selection, queryBuilder, environment));
      queryBuilder.append(" }");
    }
  }

  private static void processFieldArguments(
      Field field, StringBuilder queryBuilder, DataFetchingEnvironment environment) {
    if (!field.getArguments().isEmpty()) {
      GraphQLService service =
          environment.getGraphQlContext().get(GraphQLUtils.ENV_CURRENT_SERVICE);
      Stack<GraphQLType> currentType =
          environment.getGraphQlContext().get(GraphQLUtils.ENV_CURRENT_TYPE);

      GraphQLObjectType objectType = (GraphQLObjectType) currentType.peek();
      GraphQLServiceOverride override = service.getOverrideConfig();

      queryBuilder.append("(");
      field
          .getArguments()
          .forEach(
              arg -> {
                String argName =
                    override.getOriginalFieldArgumentName(
                        objectType.getName(), field.getName(), arg.getName());
                queryBuilder.append(argName).append(": ");
                queryBuilder.append(formatValue(arg.getValue())).append(", ");
              });
      queryBuilder.setLength(queryBuilder.length() - 2);
      queryBuilder.append(")");
    }
  }

  private static void processSelection(
      Selection<?> selection, StringBuilder queryBuilder, DataFetchingEnvironment environment) {
    if (selection instanceof Field subField) {

      Stack<GraphQLType> currentType =
          environment.getGraphQlContext().get(GraphQLUtils.ENV_CURRENT_TYPE);
      GraphQLService service =
          environment.getGraphQlContext().get(GraphQLUtils.ENV_CURRENT_SERVICE);
      GraphQLServiceOverride override = service.getOverrideConfig();

      GraphQLObjectType objectType = (GraphQLObjectType) currentType.peek();

      buildFieldQueryPart(environment, override, objectType, subField, queryBuilder);

      currentType.push(GraphQLTypeUtils.getFieldType(currentType.peek(), subField.getName()));
      processSelectionSet(subField.getSelectionSet(), queryBuilder, environment);
      currentType.pop();

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
        .scalar(
            GraphQLScalarType.newScalar()
                .name("OrderBy")
                .description("排序对象, 格式如：createdAt_ASC ")
                .coercing(new OrderCoercing())
                .build())
        .scalar(
            GraphQLScalarType.newScalar()
                .name("File")
                .coercing(ExtendedScalars.GraphQLShort.getCoercing())
                .build())
        .build();
  }

  public static GraphQLSchema mergeSchemas(
      List<GraphQLSchema> schemas, GraphQLCodeRegistry codeRegistry) {

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

    GraphQLCodeRegistry.Builder codeRegistryBuilder =
        GraphQLCodeRegistry.newCodeRegistry(codeRegistry);

    // 创建 TypeDefinitionRegistry 并添加现有模式和扩展定义
    TypeDefinitionRegistry mergedRegistry = new TypeDefinitionRegistry();

    RuntimeWiring.Builder runtimeWiringBuilder = RuntimeWiring.newRuntimeWiring();

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
      for (GraphQLFieldDefinition field : schema.getQueryType().getFields()) {
        FieldCoordinates coordinates =
            FieldCoordinates.coordinates(schema.getQueryType().getName(), field.getName());
        DataFetcher<?> dataFetcher = schema.getCodeRegistry().getDataFetcher(coordinates, field);
        codeRegistryBuilder.dataFetcher(coordinates, dataFetcher);
      }
    }

    // 构建新的GraphQL模式
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    runtimeWiringBuilder.codeRegistry(codeRegistryBuilder.build());

    // 用于扩展现有模式的SDL表示
    String extensionSchemaSDL = "extend type Query { newField: String }";
    TypeDefinitionRegistry extensionRegistry = schemaParser.parse(extensionSchemaSDL);
    mergedRegistry.merge(extensionRegistry);

    runtimeWiringBuilder.type(
        "Query", builder -> builder.dataFetcher("newField", new StaticDataFetcher("newField")));

    RuntimeWiring runtimeWiring = runtimeWiringBuilder.build();

    return schemaGenerator.makeExecutableSchema(mergedRegistry, runtimeWiring);
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
