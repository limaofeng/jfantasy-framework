package org.jfantasy.framework.util.common;

import org.jfantasy.demo.bean.User;
import org.junit.jupiter.api.Test;

public class BeanUtilTest {

  @Test
  public void testSetValue() throws Exception {}

  @Test
  public void testGetValue() throws Exception {}

  @Test
  public void testCopyProperties() throws Exception {}

  @Test
  void copyProperties() {
    User user1 = new User();
    user1.setName("limaofeng");

    User user2 = new User();
    user2.setEnabled(true);

    BeanUtil.copyProperties(user1, user2);

    assert user1.isEnabled();
  }
}
