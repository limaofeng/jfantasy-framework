package net.asany.jfantasy.graphql.gateway.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.IOException;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.graphql.gateway.GraphQLClientFactory;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.web.client.RestTemplate;

class RemoteGraphQLServiceTest {

  private final GraphQLClientFactory clientFactory =
      new DefaultGraphQLClientFactory(
          new DefaultResourceLoader(),
          new RestTemplate(),
          JSON.getObjectMapper().copy().setSerializationInclusion(JsonInclude.Include.ALWAYS));

  @Test
  void makeSchema() throws IOException {
    RemoteGraphQLService service =
        RemoteGraphQLService.builder()
            .name("asany-server")
            //            .url("https://api.asany.cn/graphql")
            //            .clientFactory(this.clientFactory)
            .build();
    service.makeSchema();
  }
}
