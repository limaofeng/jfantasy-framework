package com.thuni.his;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.graphql.client.GraphQLClient;
import org.jfantasy.graphql.client.GraphQLResponse;
import org.jfantasy.graphql.client.GraphQLTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class GraphQLTemplateTest {

    @GraphQLClient
    private GraphQLTemplate client;

    @Test
    public void get_users() throws IOException {
        GraphQLResponse response = client.postForResource("graphql/employee.graphql");
        assertNotNull(response);
        assertThat(response.isOk()).isTrue();
        assertThat(response.get("$.data.users.pageSize", Integer.class)).isEqualTo(15);
    }

}
