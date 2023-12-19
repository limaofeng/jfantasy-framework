package org.jfantasy.graphql.gateway;

import java.io.IOException;
import java.util.*;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.graphql.gateway.data.GraphQLGatewayDataFetcherFactory;
import org.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import org.jfantasy.graphql.gateway.service.GraphQLService;
import org.jfantasy.graphql.util.GraphQLUtils;
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
      .url("https://api.asany.cn/graphql")
      .ignoreField("Application", "layoutRoute")
      .renameField("Application", "name", "appName")
      .renameField("Query", "applications", "apps")
      .renameFieldArgument("Query", "applications", "where", "filter")
      .build();

    GraphQLSchema customSchema = GraphQLUtils.buildSchema("""
    type Query {
      hello: String
    }
    """);

    GraphQLGateway gateway = new GraphQLGateway(Collections.singletonList(service));

    gateway.init();


  }

  @Test
  void testMergeSchemas() throws IOException {
    RemoteGraphQLService service = RemoteGraphQLService.builder()
      .name("asany")
      .url("https://api.asany.cn/graphql")
      .ignoreField("Application", "layoutRoute")
      .renameField("Application", "name", "appName")
      .renameField("Query", "applications", "apps")
      .renameFieldArgument("Query", "applications", "where", "filter")
      .build();

    GraphQLSchema customSchema = GraphQLUtils.buildSchema("""
    type Query {
      hello: String
    }
    """);

    GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();

    codeRegistryBuilder.defaultDataFetcher(new GraphQLGatewayDataFetcherFactory());


    service.makeSchema();

    GraphQLSchema schema = GraphQLUtils.mergeSchemas(Arrays.asList(customSchema, service.getSchema()), codeRegistryBuilder.build());

    GraphQL graphQL = GraphQL.newGraphQL(schema).build();

    // 定义 GraphQL 查询字符串
    String queryStr =
      """
      query app($where: ApplicationWhereInput) {
        app1: apps(filter: $where) {
          id
          appName
          xxx: appName
#          layoutRoute(space: "web") {
#            id
#            path
#          }
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
}
