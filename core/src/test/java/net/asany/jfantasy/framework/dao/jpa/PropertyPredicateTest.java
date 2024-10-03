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
package net.asany.jfantasy.framework.dao.jpa;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.MatchType;
import org.junit.jupiter.api.Test;

@Slf4j
public class PropertyPredicateTest {

  @Test
  public void getPropertyValue() {
    PropertyPredicate filter = new PropertyPredicate(MatchType.EQ, "name", "limaofeng");
    System.out.println(filter.getPropertyValue(String.class));
  }

  @Test
  void getMatchType() {
    MatchType matchType = MatchType.get("gt");
    PropertyFilter builder = PropertyFilter.newFilter();
    assert matchType != null;
    matchType.build(builder, "age", "30");
    List<PropertyPredicate> filters = builder.build();
    System.out.println(filters.size());
  }

  @Test
  void getMatchTypeOfNotStartsWith() {
    MatchType matchType = MatchType.get("notStartsWith");
    System.out.println(matchType);
  }

  @Test
  void testToString() {
    List<PropertyPredicate> filters = PropertyFilter.newFilter().in("aaa", "12").build();
    log.debug(filters.toString());
  }
}
