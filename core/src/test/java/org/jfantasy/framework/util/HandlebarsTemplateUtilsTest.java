package org.jfantasy.framework.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.models.User;
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
  void testProcessTemplateIntoString() {}

  @Test
  void writer() {}

  @Test
  void template() {}
}
