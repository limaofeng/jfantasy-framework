package net.asany.jfantasy.framework.util.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

@Slf4j
public class DateUtilTest {

  @BeforeEach
  public void setUp() throws Exception {
    log.debug("TimeZone : " + TimeZone.getDefault().getID());
  }

  @Test
  public void formatRfc822Date() throws Exception {}

  @Test
  public void format() throws Exception {
    log.info(new Date().toInstant().toString());
  }

  @Test
  public void format1() throws Exception {}

  @Test
  public void format2() throws Exception {}

  @Test
  public void format3() throws Exception {}

  @Test
  public void format5() throws Exception {}

  @Test
  public void format6() throws Exception {}

  @Test
  public void toDay() throws Exception {}

  @Test
  public void isSameDay() throws Exception {}

  @Test
  public void parse() throws Exception {
    String str = "2016-09-23 17:02:02";
    Date date = DateUtil.parse(str, "yyyy-MM-dd HH:mm:ss");
    String strNew = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
    log.debug(strNew);
    Assert.isTrue(str == strNew);
  }

  @Test
  public void dayInterval() throws Exception {
    Date day = DateUtil.now();
    long num = DateUtil.dayInterval(day, day);
    assertEquals(num, 0);
    log.debug(num + "");
  }

  @Test
  public void isWorkDay() throws Exception {}

  @Test
  public void roundTo() throws Exception {}

  @Test
  public void nextDay() throws Exception {}

  @Test
  public void nextThreeDate() throws Exception {}

  @Test
  public void nextHour() throws Exception {}

  @Test
  public void add() throws Exception {}

  @Test
  public void set() throws Exception {}

  @Test
  public void getActualMaximumTime() throws Exception {}

  @Test
  public void getActualMinimumTime() throws Exception {}

  @Test
  public void interval() throws Exception {}

  @Test
  public void intervalFormat() throws Exception {}

  @Test
  public void intervalFormat1() throws Exception {}

  @Test
  public void interval1() throws Exception {}

  @Test
  public void compare() throws Exception {}

  @Test
  public void addDay() throws Exception {}

  @Test
  public void first() throws Exception {}

  @Test
  public void last() throws Exception {}

  @Test
  public void next() throws Exception {}

  @Test
  public void prev() throws Exception {}

  @Test
  public void getLastDayOfWeek() throws Exception {}

  @Test
  public void getTimeField() throws Exception {}

  @Test
  public void getWeekOfYear() throws Exception {}

  @Test
  public void setTimeField() throws Exception {}

  @Test
  public void min() throws Exception {}

  @Test
  public void max() throws Exception {}

  @Test
  public void now() throws Exception {}

  @Test
  public void betweenDates() throws Exception {
    Date starts = DateUtil.parse("2022-01-03", "yyyy-MM-dd");
    Date ends = DateUtil.parse("2022-01-05", "yyyy-MM-dd");

    List<Date> dates = DateUtil.betweenDates(starts, ends, Calendar.DATE);

    for (Date date : dates) {
      log.debug("data ->" + DateUtil.format(date));
    }
  }

  @Test
  public void diff() throws Exception {
    Date starts = DateUtil.parse("2022-01-03", "yyyy-MM-dd");
    Date ends = DateUtil.parse("2022-01-05", "yyyy-MM-dd");
    int number = DateUtil.diff(starts, ends, Calendar.DATE);
    log.debug(String.format("number = %d%n", number));
  }

  @Test
  public void fieldValue() throws Exception {}

  @Test
  public void truncatedCompareTo() throws Exception {
    log.debug(
        ""
            + DateUtil.truncatedCompareTo(
                DateUtil.now(),
                DateUtil.parse("2017-01-19 21:21:11", "yyyy-MM-dd"),
                Calendar.DATE));
    log.debug(
        ""
            + DateUtil.truncatedCompareTo(
                DateUtil.parse("2017-02-15", "yyyy-MM-dd"), DateUtil.now(), Calendar.DATE));
  }

  @Test
  public void testFormat() throws Exception {
    log.debug(" 当前日期 :" + DateUtil.format("yyyy-MM-dd"));

    log.debug(" 上下午 :" + DateUtil.format("a") + "/" + DateUtil.format("a", Locale.US));

    log.debug(" 时间 :" + DateUtil.format("zzzz") + "/" + DateUtil.format("zzzz", Locale.US));

    log.debug(" 星期 :" + DateUtil.format("EEE") + "/" + DateUtil.format("EEE", Locale.US));

    log.debug(" 月份 :" + DateUtil.format("MMM") + "/" + DateUtil.format("MMM", Locale.US));

    log.debug(
        " 当前日期 :"
            + DateUtil.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            + "/"
            + DateUtil.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US));

    log.debug(" 当前日期 :" + DateUtil.formatRfc822Date(DateUtil.now()));
  }
}
