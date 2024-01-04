package net.asany.jfantasy.graphql.gateway.config;

import static org.junit.jupiter.api.Assertions.*;

import graphql.introspection.Introspection;
import graphql.language.DirectiveDefinition;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;
import org.junit.jupiter.api.Test;

@Slf4j
class DirectiveConfigTest {

  @Test
  void getDefinition() {
    DirectiveConfig directiveConfig =
        DirectiveConfig.builder()
            .name("dateformat")
            .description("日期格式化")
            .repeatable(true)
            .locations(
                Introspection.DirectiveLocation.FIELD_DEFINITION,
                Introspection.DirectiveLocation.FIELD)
            .argument("format", "String!", "日期格式", "{defaultValue:\"yyyy-MM-dd\"}")
            .build();
    log.info("definition: {}", directiveConfig.getDefinition());
    DirectiveDefinition directiveDefinition =
        GraphQLTypeUtils.parseDirectiveDefinition(directiveConfig.getDefinition());
    assertEquals(directiveDefinition.getName(), "dateformat");
  }
}
