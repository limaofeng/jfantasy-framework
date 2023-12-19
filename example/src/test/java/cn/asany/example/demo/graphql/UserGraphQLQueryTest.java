package cn.asany.example.demo.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.asany.example.TestApplication;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.graphql.client.GraphQLResponse;
import org.jfantasy.graphql.client.GraphQLTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class UserGraphQLQueryTest {

  @Autowired private GraphQLTemplate graphQLTemplate;

  @Test
  public void get_users() throws IOException {
    GraphQLResponse response = graphQLTemplate.postForResource("graphql/users.graphql");
    assertNotNull(response);
    assertThat(response.isOk()).isTrue();
    assertThat(response.get("$.data.users.pageSize", Integer.class)).isEqualTo(15);
  }
}
