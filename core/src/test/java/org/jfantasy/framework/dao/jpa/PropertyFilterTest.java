package org.jfantasy.framework.dao.jpa;

import java.util.List;
import org.junit.jupiter.api.Test;

public class PropertyFilterTest {

  @Test
  public void getPropertyValue() throws Exception {
    PropertyFilter filter = new PropertyFilter(PropertyFilter.MatchType.EQ, "name", "limaofeng");
    System.out.println(filter.getPropertyValue(String.class));
  }

  @Test
  void getMatchType() {
    PropertyFilter.MatchType matchType = PropertyFilter.MatchType.get("gt");
    PropertyFilterBuilder builder = PropertyFilter.builder();
    matchType.build(builder, "age", "30");
    List<PropertyFilter> filters = builder.build();
    System.out.println(filters.size());
  }
}
