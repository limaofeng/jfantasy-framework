package org.jfantasy.schedule.service;

import org.quartz.*;

import java.util.Map;
import java.util.TimeZone;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class ScheduleHelper {

  /**
   * 添加任务
   *
   * @param jobClass JobClass
   * @param jobName key
   * @param group key
   */
  public static JobBuilder newJob(Class<? extends Job> jobClass, String jobName, String group) {
    return JobBuilder.newJob(jobClass).withIdentity(jobName, group).storeDurably(true);
  }

  public static JobBuilder newJob(
      Class<? extends Job> jobClass, String jobName, String group, Map<String, String> data) {
    return JobBuilder.newJob(jobClass)
        .withIdentity(jobName, group)
        .storeDurably(true)
        .setJobData(jobData(data));
  }

  public static JobDataMap jobData(Map<String, String> data) {
    if (data == null) {
      return new JobDataMap();
    }
    return new JobDataMap(data);
  }

  public static TriggerBuilder<Trigger> newTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      @SuppressWarnings("rawtypes") ScheduleBuilder schedule) {
    return TriggerBuilder.newTrigger()
        .forJob(jobKey)
        .withIdentity(triggerKey)
        .withSchedule(schedule);
  }

  public static TriggerBuilder<Trigger> newTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      @SuppressWarnings("rawtypes") ScheduleBuilder schedule,
      String description) {
    return newTrigger(jobKey, triggerKey, schedule).withDescription(description);
  }

  public static TriggerBuilder<Trigger> newTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      @SuppressWarnings("rawtypes") ScheduleBuilder schedule,
      Map<String, String> args) {
    return newTrigger(jobKey, triggerKey, schedule).usingJobData(jobData(args));
  }

  public static TriggerBuilder<Trigger> newTrigger(
    JobKey jobKey, TriggerKey triggerKey, Map<String, String> args) {
    return TriggerBuilder.newTrigger()
      .forJob(jobKey)
      .withIdentity(triggerKey)
      .usingJobData(jobData(args));
  }

  public static TriggerBuilder<Trigger> newTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      ScheduleBuilder<Trigger> schedule,
      String description,
      Map<String, String> args) {
    return newTrigger(jobKey, triggerKey, schedule)
        .withDescription(description)
        .usingJobData(jobData(args));
  }

  public static CronScheduleBuilder cron(String cron) {
    return cronSchedule(cron);
  }

  /**
   * 添加任务的触发器
   *
   * @param cron 任务表达式
   * @param timeZone 时区
   * @return TriggerBuilder<CronTrigger>
   */
  public static CronScheduleBuilder cron(String cron, TimeZone timeZone) {
    return cronSchedule(cron).inTimeZone(timeZone);
  }

  /**
   * 添加触发器
   *
   * @param interval 触发间隔时间
   * @param repeatCount 触发次数(次数为0触发一次)
   * @return SimpleScheduleBuilder
   */
  public static SimpleScheduleBuilder simple(long interval, int repeatCount) {
    return simpleSchedule().withIntervalInMilliseconds(interval).withRepeatCount(repeatCount);
  }

  public static DailyTimeIntervalScheduleBuilder dailyTimeInterval(
      int interval, DateBuilder.IntervalUnit unit, int repeatCount) {
    return dailyTimeIntervalSchedule().withInterval(interval, unit).withRepeatCount(repeatCount);
  }

  public static DailyTimeIntervalScheduleBuilder dailyTimeInterval(
      int interval, DateBuilder.IntervalUnit unit, TimeOfDay timeOfDay, int repeatCount) {
    return dailyTimeIntervalSchedule()
        .withInterval(interval, unit)
        .startingDailyAt(timeOfDay)
        .withRepeatCount(repeatCount);
  }

  public static CalendarIntervalScheduleBuilder calendarInterval(
      int interval, DateBuilder.IntervalUnit unit) {
    return calendarIntervalSchedule().withInterval(interval, unit);
  }

  /**
   * 返回 各时段的表达式
   *
   * @param cron 表达式
   * @param i    下标
   * @return string
   */
  public static String cron(String cron, int i) {
    String str = "";
    if ("".equals(cron) || cron == null) {
      return str;
    }
    String[] cronArray = cron.split(" ");
    for (int a = 0; a < cronArray.length; a++) {
      if (i == a) {
        str = cronArray[a];
        break;
      }
    }
    return str;
  }
}
