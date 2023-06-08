package org.jfantasy.framework.dao.jpa;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class PropertyFilterTest {

  static class UserPropertyFilter extends PropertyFilterBuilder implements PropertyFilter {
    UserPropertyFilter() {
      super(new ArrayList<>());
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
  }
}
