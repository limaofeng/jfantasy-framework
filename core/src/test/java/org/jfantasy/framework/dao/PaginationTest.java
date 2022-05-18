package org.jfantasy.framework.dao;

import org.jfantasy.framework.jackson.models.User;
import org.junit.jupiter.api.Test;

class PaginationTest {

  @Test
  void builder() {
    Pagination<User> pager = Pagination.newPager();
    Pagination<User> pager1 = Pagination.newPager(1);
    Pagination<User> pager2 = Pagination.newPager(2, 1);
    Pagination<User> pager3 = Pagination.newPager(2, 1, OrderBy.asc("uid"));
    Pagination<User> pager4 = Pagination.newPager(OrderBy.asc("uid"));
    Pagination<User> pager5 = Pagination.newPager(5, OrderBy.asc("uid"));
    Pagination<User> pager6 = Pagination.newPager(pager5);
  }
}
