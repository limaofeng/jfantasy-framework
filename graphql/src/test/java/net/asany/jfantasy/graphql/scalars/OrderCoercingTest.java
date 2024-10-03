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
package net.asany.jfantasy.graphql.scalars;

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
