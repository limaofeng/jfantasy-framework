/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
