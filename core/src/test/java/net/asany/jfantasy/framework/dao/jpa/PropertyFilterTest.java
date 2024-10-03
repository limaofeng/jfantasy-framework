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

import java.util.ArrayList;
import java.util.List;
import net.asany.jfantasy.framework.dao.MatchType;
import net.asany.jfantasy.framework.error.User;
import org.junit.jupiter.api.Test;

class PropertyFilterTest {

  static class UserPropertyFilter extends PropertyFilterBuilder<UserPropertyFilter, List<String>>
      implements PropertyFilter {

    UserPropertyFilter() {
      super(new ArrayList<>());
      this.custom(
          "name",
          new MatchType[] {MatchType.EQ},
          (name, matchType, value, context) -> {
            if (matchType == MatchType.EQ) {
              context.add(value.toString());
            }
          });
    }

    public static PropertyFilter newFilter() {
      return new UserPropertyFilter();
    }

    @Override
    public String build() {
      return "";
    }
  }

  @Test
  void builder() {
    PropertyFilter filter = UserPropertyFilter.newFilter().equal("name", "123");
    filter.build();

    PropertyFilter.register(User.class, UserPropertyFilter.class);

    PropertyFilter userFilter = PropertyFilter.newFilter(User.class);

    userFilter.equal("name", "123").build();

    PropertyFilter.newFilter().equal("name", "123").build();
    PropertyFilter.newFilter().contains("", "123").build();
  }
}
