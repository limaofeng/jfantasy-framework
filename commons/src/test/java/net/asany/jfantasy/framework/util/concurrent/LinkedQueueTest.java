/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.util.concurrent;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.models.User;
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
