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
package net.asany.jfantasy.graphql.client.error;

import static org.junit.jupiter.api.Assertions.*;

import net.asany.jfantasy.framework.jackson.JSON;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataFetchGraphQLErrorTest {

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {}

  @Test
  public void jsonToError() {
    String body =
        "{\"locations\":[{\"line\":1,\"column\":16}],\"path\":[\"viewer\"],\"extensions\":{\"classification\":\"AuthenticatedError\",\"code\":\"100401\",\"timestamp\":\"2024-06-20 15:10:42\"}}";
    DataFetchGraphQLError error = JSON.deserialize(body, DataFetchGraphQLError.class);
    assertEquals("100401", error.getExtensions().get("code"));
    assertEquals("2024-06-20 15:10:42", error.getExtensions().get("timestamp"));
  }
}
