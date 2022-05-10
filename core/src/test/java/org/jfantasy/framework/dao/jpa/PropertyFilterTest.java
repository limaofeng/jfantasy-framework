package org.jfantasy.framework.dao.jpa;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PropertyFilterTest {

  @Test
  public void getPropertyValue() {
    PropertyFilter filter = new PropertyFilter(PropertyFilter.MatchType.EQ, "name", "limaofeng");
    System.out.println(filter.getPropertyValue(String.class));
  }

  @Test
  void getMatchType() {
    PropertyFilter.MatchType matchType = PropertyFilter.MatchType.get("gt");
    PropertyFilterBuilder builder = PropertyFilter.builder();
    assert matchType != null;
    matchType.build(builder, "age", "30");
    List<PropertyFilter> filters = builder.build();
    System.out.println(filters.size());
  }

  @Test
  void getMatchTypeOfNotStartsWith() {
    PropertyFilter.MatchType matchType = PropertyFilter.MatchType.get("notStartsWith");
    System.out.println(matchType);
  }

  @Test
  void testToString() {
    List<PropertyFilter> filters = PropertyFilter.builder().in("aaa", "12").build();
    log.debug(filters.toString());
  }
}
