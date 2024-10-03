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
package net.asany.jfantasy.framework.util;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.User;
import org.junit.jupiter.api.Test;

@Slf4j
class HandlebarsTemplateUtilsTest {

  @Test
  void processTemplateIntoString() {
    Map<String, Object> data = new HashMap<>();
    data.put("name", "limaofeng");
    data.put("user", User.builder().name("limaofeng").age(38).build());
    String content =
        HandlebarsTemplateUtils.processTemplateIntoString(
            "hello: {{user.name}}, {{user.age}} years old", data);
    log.info("content: {}", content);
  }

  @Test
  void testDate() {
    Map<String, Object> data = new HashMap<>();
    data.put("date", new java.util.Date());
    String content =
        HandlebarsTemplateUtils.processTemplateIntoString(
            "hello: {{formatDate date 'yyyy-MM-dd HH:mm:ss'}}", data);
    log.info("content: {}", content);
  }

  @Test
  void testProcessTemplateIntoString() {}

  @Test
  void writer() {}

  @Test
  void template() {}
}
