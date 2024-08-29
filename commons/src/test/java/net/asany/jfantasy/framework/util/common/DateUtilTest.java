package net.asany.jfantasy.framework.util.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class DateUtilTest {

  @BeforeEach
  public void setUp() {
    log.debug("TimeZone : {} ", TimeZone.getDefault().getID());
  }

  @Test
  public void formatRfc822Date() {}

  @Test
  public void format() {
    log.info(new Date().toInstant().toString());
  }

  @Test
  public void format1() {}

  @Test
  public void format2() {}

  @Test
  public void format3() {}

  @Test
  public void format5() {}

  @Test
  public void format6() {}

  @Test
  public void toDay() {}

  @Test
  public void isSameDay() {}

  @Test
  public void parse() {
    String str = "2016-09-23 17:02:02";
    Date date = DateUtil.parse(str, "yyyy-MM-dd HH:mm:ss");
    String strNew = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
    log.debug(strNew);
    assertEquals(str, strNew);
  }

  @Test
  public void dayInterval() {
    Date day = DateUtil.now();
    long num = DateUtil.dayInterval(day, day);
    assertEquals(num, 0);
    log.debug(num + "");
  }

  @Test
  public void isWorkDay() {}

  @Test
  public void roundTo() {}

  @Test
  public void nextDay() {}

  @Test
  public void nextThreeDate() {}

  @Test
  public void nextHour() {}

  @Test
  public void add() {}

  @Test
  public void set() {}

  @Test
  public void getActualMaximumTime() {}

  @Test
  public void getActualMinimumTime() {}

  @Test
  public void interval() {}

  @Test
  public void intervalFormat() {}

  @Test
  public void intervalFormat1() {}

  @Test
  public void interval1() {}

  @Test
  public void compare() {}

  @Test
  public void addDay() {}

  @Test
  public void first() {}

  @Test
  public void last() {}

  @Test
  public void next() {}

  @Test
  public void prev() {}

  @Test
  public void getLastDayOfWeek() {}

  @Test
  public void getTimeField() {}

  @Test
  public void getWeekOfYear() {}

  @Test
  public void setTimeField() {}

  @Test
  public void min() {}

  @Test
  public void max() {}

  @Test
  public void now() {}

  @Test
  public void betweenDates() {
    Date starts = DateUtil.parse("2022-01-03", "yyyy-MM-dd");
    Date ends = DateUtil.parse("2022-01-05", "yyyy-MM-dd");

    List<Date> dates = DateUtil.betweenDates(starts, ends, Calendar.DATE);

    for (Date date : dates) {
      log.debug("data ->" + DateUtil.format(date));
    }
  }

  @Test
  public void diff() {
    Date starts = DateUtil.parse("2022-01-03", "yyyy-MM-dd");
    Date ends = DateUtil.parse("2022-01-05", "yyyy-MM-dd");
    int number = DateUtil.diff(starts, ends, Calendar.DATE);
    log.debug(String.format("number = %d%n", number));
  }

  @Test
  public void fieldValue() {}

  @Test
  public void truncatedCompareTo() {
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
  public void testFormat() {
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

    log.debug(" 当前日期 :{}", DateUtil.formatRfc822Date(DateUtil.now()));
  }

  @Test
  void convertTimeToInt() {
    log.debug("convertTimeToInt: {}", DateUtil.convertTimeToInt("0"));
    log.debug("convertTimeToInt: {}", DateUtil.convertTimeToInt("30s"));
    log.debug("convertTimeToInt: {}", DateUtil.convertTimeToInt("30")); // 默认30分钟 -> 1800秒
    log.debug("convertTimeToInt: {}", DateUtil.convertTimeToInt("30m", TimeUnit.MINUTES)); // 1800秒
    log.debug("convertTimeToInt: {}", DateUtil.convertTimeToInt("1h", TimeUnit.MINUTES)); // 3600秒
    log.debug("convertTimeToInt: {}", DateUtil.convertTimeToInt("2d", TimeUnit.MINUTES)); // 172800秒
  }
}
