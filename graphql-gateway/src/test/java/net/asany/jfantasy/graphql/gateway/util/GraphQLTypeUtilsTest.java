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
package net.asany.jfantasy.graphql.gateway.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import graphql.language.Type;
import org.junit.jupiter.api.Test;

class GraphQLTypeUtilsTest {

  @Test
  void parseType() {
    Type<?> type = GraphQLTypeUtils.parseReturnType("User");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "User");

    type = GraphQLTypeUtils.parseReturnType("User!");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "User!");

    type = GraphQLTypeUtils.parseReturnType("[User]");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "[User]");

    type = GraphQLTypeUtils.parseReturnType("[User]!");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "[User]!");

    type = GraphQLTypeUtils.parseReturnType("[User!]");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "[User!]");

    type = GraphQLTypeUtils.parseReturnType("[User!]!");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "[User!]!");
  }

  @Test
  void parseDirectiveDefinition() {
    GraphQLTypeUtils.parseDirectiveDefinition(
        "directive @auth(rules: [AuthRule!]!) on FIELD_DEFINITION");
  }
}
