package org.jfantasy.framework.dao.jpa;

import java.util.ArrayList;
import java.util.List;
import org.jfantasy.framework.dao.MatchType;
import org.junit.jupiter.api.Test;

class PropertyFilterTest {

  static class UserPropertyFilter extends PropertyFilterBuilder<UserPropertyFilter, List<String>>
      implements PropertyFilter {
    UserPropertyFilter() {
      super(new ArrayList<>());
      this.property(
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

    PropertyFilter.newFilter().equal("name", "123").build();
    PropertyFilter.newFilter().contains("", "123").build();
  }
}
