package cn.asany.his;

import static graphql.schema.idl.SchemaPrinter.Options.defaultOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import graphql.introspection.IntrospectionQuery;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import graphql.schema.idl.SchemaPrinter;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.graphql.client.GraphQLClient;
import org.jfantasy.graphql.client.GraphQLResponse;
import org.jfantasy.graphql.client.GraphQLTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class GraphQLTemplateTest {

  @GraphQLClient private GraphQLTemplate client;

  @Test
  public void get_users() throws IOException {
    GraphQLResponse response = client.postForResource("graphql/employee.graphql");
    assertNotNull(response);
    assertThat(response.isOk()).isTrue();
    assertThat(response.get("$.data.users.pageSize", Integer.class)).isEqualTo(15);
  }

  @Test
  public void introspection() throws IOException {
    GraphQLResponse response =
        client.post(IntrospectionQuery.INTROSPECTION_QUERY, "IntrospectionQuery");
    assertNotNull(response);
    assertThat(response.isOk()).isTrue();

    Document schemaDefinition =
        new IntrospectionResultToSchema()
            .createSchemaDefinition(response.get("$.data", HashMap.class));

    SchemaPrinter.Options noDirectivesOption = defaultOptions().includeDirectives(false);

    String result = new SchemaPrinter(noDirectivesOption).print(schemaDefinition);

    System.out.println(result);
  }
}
