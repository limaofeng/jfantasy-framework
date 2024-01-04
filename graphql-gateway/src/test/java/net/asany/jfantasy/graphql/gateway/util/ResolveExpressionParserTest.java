package net.asany.jfantasy.graphql.gateway.util;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.config.FieldResolve;
import org.junit.jupiter.api.Test;

@Slf4j
class ResolveExpressionParserTest {

  @Test
  void parse() {
    FieldResolve resolve = ResolveExpressionParser.parse("user(id:$createdBy)");
    log.info(resolve.getQuery());
  }
}
