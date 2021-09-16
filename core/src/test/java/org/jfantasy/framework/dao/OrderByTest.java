package org.jfantasy.framework.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class OrderByTest {

  @Test
  void toSort() {
    OrderBy orderBy = OrderBy.unsorted();
    assertEquals(orderBy.toSort(), Sort.unsorted());
  }
}
