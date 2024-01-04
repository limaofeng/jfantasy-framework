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
