package org.jfantasy.framework.dao.hibernate.util;

import java.time.OffsetDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.jfantasy.framework.util.common.DateUtil;
import org.junit.jupiter.api.Test;

@Slf4j
public class ReflectionUtilsTest {

  @Test
  public void convertStringToObject() {
    log.debug(DateUtil.now().toString());
    Date date = ReflectionUtils.convert("20150214T113000", Date.class);
    Asserts.check(DateUtil.format(date, "yyyyMMdd'T'HHmmss").equals("20150214T113000"), "错误");
    date = ReflectionUtils.convert("2022-02-07T00:00:00+08:00", Date.class);
    Asserts.check(
        DateUtil.format(date, "yyyy-MM-dd'T'HH:mm:ssXXX").equals("2022-02-07T00:00:00+08:00"),
        "错误");
    log.debug(ReflectionUtils.convert("20150214T113000", Date.class).toString());
    log.debug(
        ReflectionUtils.convertStringToObject("2019-04-08T08:26:00.853Z", Date.class).toString());
  }

  @Test
  public void convert() {
    Date date = ReflectionUtils.convert("2022-01-18T16:00:00.000Z", Date.class);
    log.debug(DateUtil.format(date));
    date = DateUtil.parse("2022-01-18T16:00:00.000Z", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    log.debug(DateUtil.format(date));
    date = Date.from(OffsetDateTime.parse("2022-01-18T16:00:00.000Z").toInstant());
    log.debug(DateUtil.format(date));
  }
}
