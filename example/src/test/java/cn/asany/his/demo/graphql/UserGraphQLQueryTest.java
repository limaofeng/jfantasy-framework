package cn.asany.his.demo.graphql;

import cn.asany.his.TestApplication;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class UserGraphQLQueryTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    public void get_users() throws IOException {
        GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/users.graphql");
        assertNotNull(response);
        assertThat(response.isOk()).isTrue();
        assertThat(response.get("$.data.users.pageSize", Integer.class)).isEqualTo(15);
    }

}