package org.jfantasy.schedule.service;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.StringMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务管理
 *
 * @author limaofeng
 */
@Slf4j
public class QuartzScheduler {

  private final Scheduler scheduler;

  public QuartzScheduler(@Autowired(required = false) Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * 返回 各时段的表达式
   *
   * @param cron 表达式
   * @param i 下标
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

  @Transactional
  public List<String> getJobGroupNames() {
    try {
      return this.scheduler.getJobGroupNames();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  @Transactional
  public List<String> getTriggerGroupNames() {
    try {
      return this.scheduler.getTriggerGroupNames();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  /**
   * 获取全部jobKey
   *
   * @return list<JobKey>
   */
  @Transactional
  public List<JobKey> getJobKeys() {
    List<JobKey> jobKeys = new ArrayList<>();
    try {
      for (String group : this.scheduler.getJobGroupNames()) {
        jobKeys.addAll(
            this.scheduler.getJobKeys(
                new GroupMatcher<JobKey>(group, StringMatcher.StringOperatorName.EQUALS) {}));
      }
      return jobKeys;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return jobKeys;
    }
  }

  @Transactional
  public List<Trigger> getTriggers(JobKey jobKey) {
    try {
      return (List<Trigger>) this.scheduler.getTriggersOfJob(jobKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  @Transactional
  public List<TriggerKey> getTriggers() {
    List<TriggerKey> triggerKeys = new ArrayList<>();
    try {
      for (String group : this.scheduler.getTriggerGroupNames()) {
        triggerKeys.addAll(
            this.scheduler.getTriggerKeys(
                new GroupMatcher<TriggerKey>(group, StringMatcher.StringOperatorName.EQUALS) {}));
      }
      return triggerKeys;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return triggerKeys;
    }
  }

  @Transactional
  public List<TriggerKey> getTriggers(GroupMatcher<TriggerKey> matcher) {
    List<TriggerKey> triggerKeys = new ArrayList<>();
    try {
      triggerKeys.addAll(this.scheduler.getTriggerKeys(matcher));
      return triggerKeys;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return triggerKeys;
    }
  }

  @Transactional
  public JobDetail getJobDetail(JobKey jobKey) {
    try {
      return this.scheduler.getJobDetail(jobKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Transactional
  public boolean checkExists(JobKey jobKey) {
    try {
      return this.scheduler.checkExists(jobKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return true;
    }
  }

  @Transactional
  public boolean checkExists(TriggerKey triggerKey) {
    try {
      return this.scheduler.checkExists(triggerKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void scheduleJob(JobDetail jobDetail, Trigger trigger) {
    try {
      this.scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void scheduleJob(Trigger trigger) {
    try {
      this.scheduler.scheduleJob(trigger);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void addJob(JobDetail jobDetail, boolean replace) {
    try {
      this.scheduler.addJob(jobDetail, replace);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 添加任务
   *
   * @param jobKey key
   * @param jobClass JobClass
   */
  @Transactional
  public JobDetail addJob(JobKey jobKey, Class<? extends Job> jobClass) {
    try {
      JobDetail job =
          newJob(jobClass)
              .withIdentity(jobKey.getName(), jobKey.getGroup())
              .storeDurably(true)
              .build();
      scheduler.addJob(job, true);
      scheduler.resumeJob(jobKey);
      return job;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Transactional
  public JobDetail addJob(JobKey jobKey, Class<? extends Job> jobClass, Map<String, String> data) {
    try {
      if (data == null) {
        data = new HashMap<>();
      }
      JobDetail job =
          newJob(jobClass)
              .withIdentity(jobKey.getName(), jobKey.getGroup())
              .storeDurably(true)
              .setJobData(new JobDataMap(data))
              .build();
      scheduler.addJob(job, true);
      scheduler.resumeJob(jobKey);
      return job;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private static final String emptyString = "";

  public Trigger addTrigger(JobKey jobKey, TriggerKey triggerKey, String cron) {
    return addTrigger(jobKey, triggerKey, cron, emptyString, new HashMap<>());
  }

  public Trigger addTrigger(
      JobKey jobKey, TriggerKey triggerKey, String cron, String triggerDescription) {
    return addTrigger(jobKey, triggerKey, cron, triggerDescription, new HashMap<>());
  }

  public Trigger addTrigger(
      JobKey jobKey, TriggerKey triggerKey, String cron, Map<String, String> args) {
    return this.addTrigger(jobKey, triggerKey, cron, emptyString, args);
  }

  /**
   * 添加任务的触发器
   *
   * @param jobKey jobKey
   * @param triggerKey triggerKey
   * @param cron 任务表达式
   * @param args 参数
   * @return Trigger
   */
  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      String cron,
      String triggerDescription,
      Map<String, String> args) {
    try {
      Trigger trigger =
          TriggerBuilder.newTrigger()
              .forJob(jobKey)
              .withIdentity(triggerKey)
              .usingJobData(new JobDataMap(args))
              .withDescription(triggerDescription)
              .withSchedule(cronSchedule(cron).withMisfireHandlingInstructionFireAndProceed())
              .build();
      this.scheduler.scheduleJob(trigger);
      this.scheduler.resumeTrigger(trigger.getKey());
      return trigger;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 添加触发器
   *
   * @param jobKey jobKey
   * @param triggerKey triggerKey
   * @param interval 触发间隔时间
   * @param repeatCount 触发次数(次数为0触发一次)
   * @param args 每次触发附带的额外数据
   * @return Trigger
   */
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      long interval,
      int repeatCount,
      Map<String, String> args) {
    return this.addTrigger(jobKey, triggerKey, interval, repeatCount, emptyString, args);
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      long interval,
      int repeatCount,
      String triggerDescription,
      Map<String, String> args) {
    try {
      Trigger trigger =
          TriggerBuilder.newTrigger()
              .forJob(jobKey)
              .withIdentity(triggerKey)
              .usingJobData(new JobDataMap(args))
              .withDescription(triggerDescription)
              .withSchedule(
                  simpleSchedule()
                      .withIntervalInMilliseconds(interval)
                      .withRepeatCount(repeatCount)
                      .withMisfireHandlingInstructionFireNow())
              .build();
      this.scheduler.scheduleJob(trigger);
      this.scheduler.resumeTrigger(triggerKey);
      return trigger;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      int interval,
      DateBuilder.IntervalUnit unit,
      int repeatCount,
      String triggerDescription,
      Map<String, String> args) {
    try {
      Trigger trigger =
          TriggerBuilder.newTrigger()
              .forJob(jobKey)
              .withIdentity(triggerKey)
              .usingJobData(new JobDataMap(args))
              .withDescription(triggerDescription)
              .withSchedule(
                  dailyTimeIntervalSchedule()
                      .withInterval(interval, unit)
                      .withRepeatCount(repeatCount))
              .build();
      this.scheduler.scheduleJob(trigger);
      this.scheduler.resumeTrigger(triggerKey);
      return trigger;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      int interval,
      DateBuilder.IntervalUnit unit,
      TimeOfDay timeOfDay,
      int repeatCount,
      String triggerDescription,
      Map<String, String> args) {
    try {
      Trigger trigger =
          TriggerBuilder.newTrigger()
              .forJob(jobKey)
              .withIdentity(triggerKey)
              .usingJobData(new JobDataMap(args))
              .withDescription(triggerDescription)
              .withSchedule(
                  dailyTimeIntervalSchedule()
                      .withInterval(interval, unit)
                      .startingDailyAt(timeOfDay)
                      .withRepeatCount(repeatCount))
              .build();
      this.scheduler.scheduleJob(trigger);
      this.scheduler.resumeTrigger(triggerKey);
      return trigger;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      int interval,
      DateBuilder.IntervalUnit unit,
      Map<String, String> args) {
    return this.addTrigger(jobKey, triggerKey, interval, unit, emptyString, args);
  }

  @Transactional
  public Trigger addTrigger(JobKey jobKey, TriggerKey triggerKey, Map<String, String> args) {
    try {
      Trigger trigger =
          TriggerBuilder.newTrigger()
              .forJob(jobKey)
              .withIdentity(triggerKey)
              .usingJobData(new JobDataMap(args))
              .startNow()
              .build();
      this.scheduler.scheduleJob(trigger);
      this.scheduler.resumeTrigger(triggerKey);
      return trigger;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      int interval,
      DateBuilder.IntervalUnit unit,
      String triggerDescription,
      Map<String, String> args) {
    try {
      Trigger trigger =
          TriggerBuilder.newTrigger()
              .forJob(jobKey)
              .withIdentity(triggerKey)
              .usingJobData(new JobDataMap(args))
              .withDescription(triggerDescription)
              .withSchedule(calendarIntervalSchedule().withInterval(interval, unit))
              .build();
      this.scheduler.scheduleJob(trigger);
      this.scheduler.resumeTrigger(triggerKey);
      return trigger;
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Transactional
  public Trigger.TriggerState getTriggerState(TriggerKey triggerKey) {
    try {
      return this.scheduler.getTriggerState(triggerKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 是否启动
   *
   * @return boolean
   */
  @Transactional
  public boolean isStarted() {
    try {
      return this.scheduler != null && this.scheduler.isStarted();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  /**
   * 是否关闭
   *
   * @return boolean
   */
  @Transactional
  public boolean isShutdown() {
    try {
      return this.scheduler != null && this.scheduler.isShutdown();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  /**
   * 停止 job
   *
   * @param jobName 任务名称
   * @param groupName 组名称
   */
  @Transactional
  public void pauseJob(String jobName, String groupName) {
    try {
      this.scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 恢复 job
   *
   * @param jobKey 任务名称
   */
  @Transactional
  public void resumeJob(JobKey jobKey) {
    try {
      this.scheduler.resumeJob(jobKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 删除指定的 job
   *
   * @param jobKey 任务名称
   * @return boolean
   */
  @Transactional
  public boolean deleteJob(JobKey jobKey) {
    try {
      return this.scheduler.deleteJob(jobKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  /**
   * 停止触发器
   *
   * @param triggerKey 触发器名称
   */
  @Transactional
  public void pauseTrigger(TriggerKey triggerKey) {
    try {
      this.scheduler.pauseTrigger(triggerKey); // 停止触发器
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 重启触发器
   *
   * @param triggerKey 触发器名称
   */
  @Transactional
  public void resumeTrigger(TriggerKey triggerKey) {
    try {
      this.scheduler.resumeTrigger(triggerKey); // 重启触发器
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 移除触发器
   *
   * @param triggerKey 触发器名称
   * @return boolean
   */
  @Transactional
  public boolean removeTrigger(TriggerKey triggerKey) {
    try {
      this.scheduler.pauseTrigger(triggerKey); // 停止触发器
      return this.scheduler.unscheduleJob(triggerKey); // 移除触发器
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  /** 暂停调度中所有的job任务 */
  @Transactional
  public void pauseAll() {
    try {
      scheduler.pauseAll();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  /** 恢复调度中所有的job的任务 */
  @Transactional
  public void resumeAll() {
    try {
      scheduler.resumeAll();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 中断TASK执行 job
   *
   * @param jobKey 触发器名称
   * @return boolean
   */
  @Transactional
  public boolean interrupt(JobKey jobKey) {
    try {
      return scheduler.interrupt(jobKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  /**
   * 直接执行job
   *
   * @param jobKey JobKey
   */
  @Transactional
  public void triggerJob(JobKey jobKey) {
    try {
      this.scheduler.triggerJob(jobKey);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * 直接触发job
   *
   * @param jobKey JobKey
   * @param args 执行参数
   */
  @Transactional
  public void triggerJob(JobKey jobKey, Map<String, String> args) {
    try {
      this.scheduler.triggerJob(jobKey, new JobDataMap(args));
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  @Transactional
  public void shutdown() {
    try {
      this.scheduler.shutdown();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  @Transactional
  public void clear() {
    try {
      this.scheduler.clear();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  @Transactional
  public List<JobDetail> jobs() {
    List<JobDetail> jobDetails = new ArrayList<>();
    for (JobKey jobKey : this.getJobKeys()) {
      try {
        jobDetails.add(scheduler.getJobDetail(jobKey));
      } catch (SchedulerException e) {
        log.error(e.getMessage(), e);
        if (e.getCause() instanceof ClassNotFoundException) {
          this.deleteJob(jobKey);
        }
      }
    }
    return jobDetails;
  }

  @Transactional
  public boolean isRunning(TriggerKey triggerKey) {
    try {
      List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
      return jobContexts.stream()
          .anyMatch(context -> triggerKey.equals(context.getTrigger().getKey()));
    } catch (SchedulerException e) {
      log.debug(e.getMessage(), e);
      return false;
    }
  }

  public ListenerManager getListenerManager() throws SchedulerException {
    return this.scheduler.getListenerManager();
  }
}
