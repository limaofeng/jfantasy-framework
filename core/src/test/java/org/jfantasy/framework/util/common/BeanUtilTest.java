package org.jfantasy.framework.util.common;

import org.jfantasy.framework.jackson.models.User;
import org.jfantasy.framework.util.reflect.Property;
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

    BeanUtil.copyProperties(
        user1, user2, (Property property, Object value, Object _dest) -> value != null);

    assert user1.isEnabled();
  }
}
