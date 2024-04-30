package net.asany.jfantasy.framework.jackson.deserializer;

import static org.junit.jupiter.api.Assertions.*;

import groovy.util.logging.Slf4j;
import java.util.Date;
import net.asany.jfantasy.framework.util.common.BeanUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
class DateDeserializerTest {

  private static final Logger log = LoggerFactory.getLogger(DateDeserializerTest.class);

  @Test
  void convertStringToObject() {
    Date date = BeanUtil.convertStringToObject("2024-04-09T08:53:09.000Z", Date.class);
    log.debug(date.toString());
  }
}
