package net.asany.jfantasy.graphql.gateway.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import graphql.language.Type;
import org.junit.jupiter.api.Test;

class GraphQLTypeUtilsTest {

  @Test
  void parseType() {
    Type<?> type = GraphQLTypeUtils.parseType("User");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "User");

    type = GraphQLTypeUtils.parseType("User!");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "User!");

    type = GraphQLTypeUtils.parseType("[User]");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "[User]");

    type = GraphQLTypeUtils.parseType("[User]!");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "[User]!");

    type = GraphQLTypeUtils.parseType("[User!]");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "[User!]");

    type = GraphQLTypeUtils.parseType("[User!]!");
    assertEquals(GraphQLTypeUtils.getTypeSource(type), "[User!]!");
  }
}
