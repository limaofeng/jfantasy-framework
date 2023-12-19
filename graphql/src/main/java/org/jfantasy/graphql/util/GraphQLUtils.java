package org.jfantasy.graphql.util;

import graphql.language.*;
import graphql.schema.*;
import graphql.schema.idl.*;
import java.util.*;
import java.util.stream.Collectors;
import org.jfantasy.framework.util.Stack;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.graphql.gateway.config.GraphQLServiceOverride;
import org.jfantasy.graphql.gateway.service.GraphQLService;
import org.jfantasy.graphql.gateway.service.GraphQLServiceTypeResolver;

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
        .build();
  }

  public static GraphQLSchema mergeSchemas(
      List<GraphQLSchema> schemas, GraphQLCodeRegistry codeRegistry) {

    GraphQLObjectType queryType =
        mergeRootTypes(
            "Query",
            schemas.stream().map(GraphQLSchema::getQueryType).toArray(GraphQLObjectType[]::new));
    GraphQLObjectType mutationType =
        mergeRootTypes(
            "Mutation",
            schemas.stream()
                .map(GraphQLSchema::getMutationType)
                .filter(Objects::nonNull)
                .toArray(GraphQLObjectType[]::new));

    GraphQLCodeRegistry.Builder codeRegistryBuilder =
        GraphQLCodeRegistry.newCodeRegistry(codeRegistry);

    // 合并类型解析器
    schemas.stream()
        .map(GraphQLSchema::getCodeRegistry)
        .forEach(codeRegistryBuilder::typeResolvers);

    GraphQLSchema.Builder newSchemaBuilder = GraphQLSchema.newSchema();

    if (!mutationType.getFields().isEmpty()) {
      newSchemaBuilder.mutation(mutationType);
    }

    for (GraphQLSchema schema : schemas) {
      for (GraphQLFieldDefinition field : schema.getQueryType().getFields()) {
        FieldCoordinates coordinates =
            FieldCoordinates.coordinates(schema.getQueryType().getName(), field.getName());
        DataFetcher<?> dataFetcher = schema.getCodeRegistry().getDataFetcher(coordinates, field);
        codeRegistryBuilder.dataFetcher(coordinates, dataFetcher);
      }
    }

    // 构建新的 GraphQLSchema
    GraphQLSchema mergedSchema =
        newSchemaBuilder
            .query(queryType)
            .mutation(mutationType)
            .codeRegistry(codeRegistryBuilder.build())
            .build();

    // 使用SchemaPrinter获取现有模式的SDL表示
    SchemaPrinter schemaPrinter =
        new SchemaPrinter(
            SchemaPrinter.Options.defaultOptions()
                .includeScalarTypes(true) // 根据需要包括标量类型
                .includeSchemaDefinition(true) // 包括模式定义
                .includeDirectives(true) // 包括指令
            );

    // 解析现有模式的SDL表示
    SchemaParser schemaParser = new SchemaParser();

    // 构建新的GraphQL模式
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    RuntimeWiring.Builder runtimeWiringBuilder =
        RuntimeWiring.newRuntimeWiring().codeRegistry(mergedSchema.getCodeRegistry());

    // 创建 TypeDefinitionRegistry 并添加现有模式和扩展定义
    TypeDefinitionRegistry mergedRegistry = new TypeDefinitionRegistry();
    mergedRegistry.merge(schemaParser.parse(schemaPrinter.print(mergedSchema)));

    // 用于扩展现有模式的SDL表示
    String extensionSchemaSDL = "extend type Query { newField: String }";
    TypeDefinitionRegistry extensionRegistry = schemaParser.parse(extensionSchemaSDL);
    mergedRegistry.merge(extensionRegistry);

    runtimeWiringBuilder.type(
        "Query", builder -> builder.dataFetcher("newField", new StaticDataFetcher("newField")));

    for (GraphQLNamedType type : mergedSchema.getAllTypesAsList()) {
      if (type instanceof GraphQLScalarType scalarType) {
        runtimeWiringBuilder.scalar(scalarType);
      } else if (type instanceof GraphQLInterfaceType interfaceType) {
        runtimeWiringBuilder.type(
            TypeRuntimeWiring.newTypeWiring(interfaceType.getName())
                .typeResolver(new GraphQLServiceTypeResolver(null)));
      } else if (type instanceof GraphQLUnionType unionType) {
        runtimeWiringBuilder.type(
            TypeRuntimeWiring.newTypeWiring(unionType.getName())
                .typeResolver(new GraphQLServiceTypeResolver(null)));
      }
    }

    RuntimeWiring runtimeWiring = runtimeWiringBuilder.build();

    return schemaGenerator.makeExecutableSchema(mergedRegistry, runtimeWiring);
  }

  private static GraphQLObjectType mergeRootTypes(String name, GraphQLObjectType... objectTypes) {
    // 合并字段
    List<GraphQLFieldDefinition> fields =
        Arrays.stream(objectTypes).flatMap(type -> type.getFieldDefinitions().stream()).toList();

    // 创建新的 GraphQLObjectType
    return GraphQLObjectType.newObject().name(name).fields(fields).build();
  }
}
