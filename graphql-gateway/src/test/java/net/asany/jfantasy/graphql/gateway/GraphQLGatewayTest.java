package net.asany.jfantasy.graphql.gateway;

import java.io.IOException;
import java.util.*;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.graphql.gateway.config.SchemaOverride;
import net.asany.jfantasy.graphql.gateway.data.GatewayDataFetcherFactory;
import net.asany.jfantasy.graphql.gateway.directive.DirectiveFactory;
import net.asany.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import net.asany.jfantasy.graphql.gateway.service.GraphQLService;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProviderFactory;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeResolver;
import net.asany.jfantasy.graphql.gateway.util.GraphQLUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphQLGatewayTest {

  @BeforeEach
  void setUp() {
    JSON.initialize();
  }

  @AfterEach
  void tearDown() {}

  void testCreateGateway() throws IOException {
    GraphQLService service = RemoteGraphQLService.builder()
      .name("asany")
//      .url("https://api.asany.cn/graphql")
      .excludeFields("Application", "layoutRoute")
//      .renameField("Application", "name", "appName")
//      .renameField("Query", "applications", "apps")
//      .renameFieldArgument("Query", "applications", "where", "filter")
      .build();

    GraphQLSchema customSchema = GraphQLUtils.buildSchema("""
    type Query {
      hello: String
    }
    """);

    GraphQLGateway gateway = GraphQLGateway.builder().addService(service).build();

    gateway.init();


  }

  @Test
  void testMergeSchemas() throws IOException {
    RemoteGraphQLService service = RemoteGraphQLService.builder()
      .name("asany")
//      .url("https://api.asany.cn/graphql")
      .excludeFields("Application", "layoutRoute")
//      .renameField("Application", "name", "appName")
//      .renameField("Query", "applications", "apps")
//      .renameFieldArgument("Query", "applications", "where", "filter")
//      .clientFactory(GraphQLClientFactory.DEFAULT)
      .scalarTypeResolver(new ScalarTypeResolver(new ScalarTypeProviderFactory()))
      .build();

    GraphQLSchema customSchema = GraphQLUtils.buildSchema("""
    type Query {
      hello: String
#      xuser(order: OrderBy): File
    }
#    scalar OrderBy
#    scalar File
    """);

    GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();

    codeRegistryBuilder.defaultDataFetcher(new GatewayDataFetcherFactory());

    service.makeSchema();

    GraphQLSchema schema = GraphQLUtils.mergeSchemas(Arrays.asList(service.getSchema(), customSchema), SchemaOverride.builder().build(), new GatewayDataFetcherFactory(), new DirectiveFactory());

    GraphQL graphQL = GraphQL.newGraphQL(schema).build();

    // 定义 GraphQL 查询字符串
    String queryStr =
      """
      query app($where: ApplicationWhereInput) {
        app1: apps(filter: $where) {
          id
          appName
          xxx: appName
          layoutRoute(space: "web") {
            id
            path
          }
        }
        newField
      }
      """;

    Map<String, Object> variables = new HashMap<>();
    variables.put("where", Map.of("enabled", Boolean.TRUE));

    // 执行查询
    ExecutionResult result = graphQL.execute(ExecutionInput.newExecutionInput(queryStr)
        .operationName("app")
      .variables(variables));

    if (!result.getErrors().isEmpty()) {
      System.out.println(result.getErrors());
    }

    // 获取并输出查询结果
    System.out.println(Optional.ofNullable(result.getData()));

    queryStr = """
    query {
      hello
    }
    """;

    result = graphQL.execute(queryStr);
    System.out.println(Optional.ofNullable(result.getData()));
  }

  @Test
  public void testLoadConfig() throws IOException {
    GraphQLGateway gateway = GraphQLGateway.builder().config("graphql-gateway.yaml").build();

    gateway.init();

    GraphQL graphQL = GraphQL.newGraphQL(gateway.getSchema()).build();

    // 定义 GraphQL 查询字符串
    String queryStr =
      """
      query app(
        $where: ApplicationWhereInput
        $space: ID
        ) {
        app1: apps(filter: $where) {
          id
          appName
          xxx: appName
          layoutRoute(space: $space) {
            id
            path
          }
        }
#        newField
      }
      """;

    Map<String, Object> variables = new HashMap<>();
    variables.put("where", Map.of("enabled", Boolean.TRUE));
    variables.put("space", "web");

    // 执行查询
    ExecutionResult result = graphQL.execute(ExecutionInput.newExecutionInput(queryStr)
      .operationName("app")
      .variables(variables));

    if (!result.getErrors().isEmpty()) {
      System.out.println(result.getErrors());
    }

    // 获取并输出查询结果
    System.out.println(Optional.ofNullable(result.getData()));


  }


  @Test
  public void testChangeFieldType() throws IOException {
    GraphQLGateway gateway = GraphQLGateway.builder().config("graphql-gateway.yaml").build();

    gateway.init();

    GraphQL graphQL = GraphQL.newGraphQL(gateway.getSchema()).build();

    // 定义 GraphQL 查询字符串
    String queryStr =
      """
      query {
          storages {
             id
             name
             createdAt
             createdBy {
                id
                name
             }
          }
      }
      """;

    // 执行查询
    ExecutionResult result = graphQL.execute(queryStr);

    if (!result.getErrors().isEmpty()) {
      System.out.println(result.getErrors());
    }

    // 获取并输出查询结果
    System.out.println(Optional.ofNullable(result.getData()));

    // TODO: 测试 dict(code: $type, type: client_type)

    // TODO: 测试 dict(code: $type, type: {client_type}) 包含对象的参数

    // TODO: 测试 dict(code: $type, type: [] or Enum or 自定义标量)

  }

  @Test
  public void testChangeFieldArgumentType() throws IOException {
    GraphQLGateway gateway = GraphQLGateway.builder().config("graphql-gateway.yaml").build();

    gateway.init();

    GraphQL graphQL = GraphQL.newGraphQL(gateway.getSchema()).build();

    // 定义 GraphQL 查询字符串
    String queryStr =
      """
        {
           apps(filter: { enabled: true }) {
             id
             appName @myDirective(arg1: "1") @myDirective(arg1: "2")
             layoutRoute(space: "web") {
               id
               path
             }
             clientSecrets {
               id
               type {
                 id
                 name
                 description
               }
               secret
             }
           }
         }
        """;

    // 执行查询
    ExecutionResult result = graphQL.execute(queryStr);

    if (!result.getErrors().isEmpty()) {
      System.out.println(result.getErrors());
    }

    // 获取并输出查询结果
    System.out.println(Optional.ofNullable(result.getData()));
  }

}
