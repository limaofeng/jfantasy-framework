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
package net.asany.jfantasy.framework.util.common;

import net.asany.jfantasy.demo.bean.User;
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
