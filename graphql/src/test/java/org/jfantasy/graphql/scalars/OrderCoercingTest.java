package org.jfantasy.graphql.scalars;

import static org.junit.jupiter.api.Assertions.*;

import graphql.language.StringValue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

@Slf4j
class OrderCoercingTest {

  private final OrderCoercing coercing = new OrderCoercing();

  @Test
  void serialize() {
    String sortString = coercing.serialize(Sort.by(Sort.Direction.DESC, "name"));
    log.info("sort to string:" + sortString);
  }

  @Test
  void parseValue() {
    Sort sort1 = coercing.parseValue("name_asc");
    log.info("sort:" + sort1);

    Sort sort2 = coercing.parseValue("name_asc(NULLS_LAST)");
    log.info("sort:" + sort2);
  }

  @Test
  void parseLiteral() {
    Sort sort1 = coercing.parseValue(StringValue.newStringValue("name_asc"));
    log.info("sort:" + sort1);

    Sort sort2 = coercing.parseValue(StringValue.newStringValue("name_asc(NULLS_LAST)"));
    log.info("sort:" + sort2);
  }
}
