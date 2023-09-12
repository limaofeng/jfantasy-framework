package org.jfantasy.framework.util.concurrent;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.models.User;
import org.junit.jupiter.api.Test;

@Slf4j
public class LinkedQueueTest {

  @Test
  public void testRemove() throws Exception {
    LinkedQueue<User> queue = new LinkedQueue<User>();
    User user = new User();
    // 添加数据
    queue.put(user);

    assertTrue(queue.remove(user));

    assertTrue(queue.size() == 0);
  }

  @Test
  public void testIterator() throws Exception {
    LinkedQueue<User> queue = new LinkedQueue<User>();
    User _u1 =
        new User() {
          {
            this.setNickName("test-1");
          }
        };
    User _u2 =
        new User() {
          {
            this.setNickName("test-2");
          }
        };
    User _u3 =
        new User() {
          {
            this.setNickName("test-3");
          }
        };
    // 添加数据
    queue.add(_u1);
    queue.add(_u2);
    queue.add(_u3);

    queue.remove(_u3);
    queue.remove(_u1);

    log.error("queue size : " + queue.size());

    for (User user : queue) {
      log.error(user.getNickName());
      assertTrue(user.getNickName() == "test-2");
    }
  }
}
