package org.jfantasy.framework.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.jfantasy.framework.jackson.models.User;
import org.junit.jupiter.api.Test;

class PagerTest {

  @Test
  void builder() {
    Pager<User> pager = Pager.newPager();
    Pager<User> pager1 = Pager.newPager(1);
    Pager<User> pager2 = Pager.newPager(2, 1);
    Pager<User> pager3 = Pager.newPager(2, 1, OrderBy.asc("uid"));
    Pager<User> pager4 = Pager.newPager(OrderBy.asc("uid"));
    Pager<User> pager5 = Pager.newPager(5, OrderBy.asc("uid"));
    Pager<User> pager6 = Pager.newPager(pager5);
  }
}
