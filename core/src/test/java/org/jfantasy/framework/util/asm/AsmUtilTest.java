package org.jfantasy.framework.util.asm;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class AsmUtilTest {

  @Test
  void makeClass() {
    Class newClass =
        AsmUtil.makeClass(
            "org.jfantasy.framework.util.asm.DemoBean",
            Property.builder().name("name").type(String.class).build());
    log.debug("newClass" + newClass.getName());
  }
}
