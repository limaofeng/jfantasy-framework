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
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务管理
 *
 * @author limaofeng
 */
@Slf4j
public class SchedulerUtils {

  private final Scheduler scheduler;

  public SchedulerUtils(Scheduler scheduler) {
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

  @Transactional(readOnly = true)
  public List<String> getJobGroupNames() throws SchedulerException {
    return this.scheduler.getJobGroupNames();
  }

  @Transactional(readOnly = true)
  public List<String> getTriggerGroupNames() throws SchedulerException {
    return this.scheduler.getTriggerGroupNames();
  }

  /**
   * 获取全部jobKey
   *
   * @return list<JobKey>
   */
  @Transactional(readOnly = true)
  public List<JobKey> getJobKeys() throws SchedulerException {
    List<JobKey> jobKeys = new ArrayList<>();
    for (String group : this.scheduler.getJobGroupNames()) {
      jobKeys.addAll(
          this.scheduler.getJobKeys(
              new GroupMatcher<JobKey>(group, StringMatcher.StringOperatorName.EQUALS) {}));
    }
    return jobKeys;
  }

  @Transactional(readOnly = true)
  public List<? extends Trigger> getTriggers(JobKey jobKey) throws SchedulerException {
    return this.scheduler.getTriggersOfJob(jobKey);
  }

  @Transactional(readOnly = true)
  public List<TriggerKey> getTriggers() throws SchedulerException {
    List<TriggerKey> triggerKeys = new ArrayList<>();
    for (String group : this.scheduler.getTriggerGroupNames()) {
      triggerKeys.addAll(
          this.scheduler.getTriggerKeys(
              new GroupMatcher<TriggerKey>(group, StringMatcher.StringOperatorName.EQUALS) {}));
    }
    return triggerKeys;
  }

  @Transactional(readOnly = true)
  public List<TriggerKey> getTriggers(GroupMatcher<TriggerKey> matcher) throws SchedulerException {
    return new ArrayList<>(this.scheduler.getTriggerKeys(matcher));
  }

  @Transactional(readOnly = true)
  public JobDetail getJobDetail(JobKey jobKey) throws SchedulerException {
    return this.scheduler.getJobDetail(jobKey);
  }

  @Transactional(readOnly = true)
  public boolean checkExists(JobKey jobKey) throws SchedulerException {
    return this.scheduler.checkExists(jobKey);
  }

  @Transactional(readOnly = true)
  public boolean checkExists(TriggerKey triggerKey) throws SchedulerException {
    return this.scheduler.checkExists(triggerKey);
  }

  @Transactional(rollbackFor = Exception.class)
  public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
    this.scheduler.scheduleJob(jobDetail, trigger);
  }

  @Transactional(rollbackFor = Exception.class)
  public void scheduleJob(Trigger trigger) throws SchedulerException {
    this.scheduler.scheduleJob(trigger);
  }

  @Transactional(rollbackFor = Exception.class)
  public void rescheduleJob(Trigger trigger) throws SchedulerException {
    this.scheduler.rescheduleJob(trigger.getKey(), trigger);
  }

  @Transactional(rollbackFor = Exception.class)
  public void rescheduleJob(TriggerKey triggerKey, Trigger newTrigger) throws SchedulerException {
    scheduler.rescheduleJob(triggerKey, newTrigger);
  }

  @Transactional(rollbackFor = Exception.class)
  public void addJob(JobDetail jobDetail, boolean replace) throws SchedulerException {
    this.scheduler.addJob(jobDetail, replace);
  }

  /**
   * 添加任务
   *
   * @param jobKey key
   * @param jobClass JobClass
   */
  @Transactional
  public JobDetail addJob(JobKey jobKey, Class<? extends Job> jobClass) throws SchedulerException {
    JobDetail job =
        newJob(jobClass)
            .withIdentity(jobKey.getName(), jobKey.getGroup())
            .storeDurably(true)
            .build();
    scheduler.addJob(job, true);
    scheduler.resumeJob(jobKey);
    return job;
  }

  @Transactional
  public JobDetail addJob(JobKey jobKey, Class<? extends Job> jobClass, Map<String, String> data)
      throws SchedulerException {
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
  }

  private static final String emptyString = "";

  @Transactional
  public Trigger addTrigger(JobKey jobKey, TriggerKey triggerKey, String cron)
      throws SchedulerException {
    return addTrigger(jobKey, triggerKey, cron, emptyString, new HashMap<>());
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey, TriggerKey triggerKey, String cron, String triggerDescription)
      throws SchedulerException {
    return addTrigger(jobKey, triggerKey, cron, triggerDescription, new HashMap<>());
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey, TriggerKey triggerKey, String cron, Map<String, String> args)
      throws SchedulerException {
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
      Map<String, String> args)
      throws SchedulerException {
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
  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      long interval,
      int repeatCount,
      Map<String, String> args)
      throws SchedulerException {
    return this.addTrigger(jobKey, triggerKey, interval, repeatCount, emptyString, args);
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      long interval,
      int repeatCount,
      String triggerDescription,
      Map<String, String> args)
      throws SchedulerException {
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
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      int interval,
      DateBuilder.IntervalUnit unit,
      int repeatCount,
      String triggerDescription,
      Map<String, String> args)
      throws SchedulerException {
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
      Map<String, String> args)
      throws SchedulerException {
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
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      int interval,
      DateBuilder.IntervalUnit unit,
      Map<String, String> args)
      throws SchedulerException {
    return this.addTrigger(jobKey, triggerKey, interval, unit, emptyString, args);
  }

  @Transactional
  public Trigger addTrigger(JobKey jobKey, TriggerKey triggerKey, Map<String, String> args)
      throws SchedulerException {
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
  }

  @Transactional
  public Trigger addTrigger(
      JobKey jobKey,
      TriggerKey triggerKey,
      int interval,
      DateBuilder.IntervalUnit unit,
      String triggerDescription,
      Map<String, String> args)
      throws SchedulerException {
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
  }

  @Transactional
  public Trigger.TriggerState getTriggerState(TriggerKey triggerKey) throws SchedulerException {
    return this.scheduler.getTriggerState(triggerKey);
  }

  /**
   * 是否启动
   *
   * @return boolean
   */
  @Transactional
  public boolean isStarted() throws SchedulerException {
    return this.scheduler != null && this.scheduler.isStarted();
  }

  /**
   * 是否关闭
   *
   * @return boolean
   */
  @Transactional
  public boolean isShutdown() throws SchedulerException {
    return this.scheduler != null && this.scheduler.isShutdown();
  }

  /**
   * 停止 job
   *
   * @param jobName 任务名称
   * @param groupName 组名称
   */
  @Transactional
  public void pauseJob(String jobName, String groupName) throws SchedulerException {
    this.scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
  }

  /**
   * 恢复 job
   *
   * @param jobKey 任务名称
   */
  @Transactional
  public void resumeJob(JobKey jobKey) throws SchedulerException {
    this.scheduler.resumeJob(jobKey);
  }

  /**
   * 删除指定的 job
   *
   * @param jobKey 任务名称
   * @return boolean
   */
  @Transactional
  public boolean deleteJob(JobKey jobKey) throws SchedulerException {
    return this.scheduler.deleteJob(jobKey);
  }

  /**
   * 停止触发器
   *
   * @param triggerKey 触发器名称
   */
  @Transactional
  public void pauseTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.pauseTrigger(triggerKey);
  }

  /**
   * 重启触发器
   *
   * @param triggerKey 触发器名称
   */
  @Transactional
  public void resumeTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.resumeTrigger(triggerKey); // 重启触发器
  }

  /**
   * 移除触发器
   *
   * @param triggerKey 触发器名称
   * @return boolean
   */
  @Transactional
  public boolean removeTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.pauseTrigger(triggerKey); // 停止触发器
    return this.scheduler.unscheduleJob(triggerKey); // 移除触发器
  }

  /** 暂停调度中所有的job任务 */
  @Transactional
  public void pauseAll() throws SchedulerException {
    scheduler.pauseAll();
  }

  /** 恢复调度中所有的job的任务 */
  @Transactional
  public void resumeAll() throws SchedulerException {
    scheduler.resumeAll();
  }

  /**
   * 中断TASK执行 job
   *
   * @param jobKey 触发器名称
   * @return boolean
   */
  @Transactional
  public boolean interrupt(JobKey jobKey) throws UnableToInterruptJobException {
    return scheduler.interrupt(jobKey);
  }

  /**
   * 直接执行job
   *
   * @param jobKey JobKey
   */
  @Transactional
  public void triggerJob(JobKey jobKey) throws SchedulerException {
    this.scheduler.triggerJob(jobKey);
  }

  /**
   * 直接触发job
   *
   * @param jobKey JobKey
   * @param args 执行参数
   */
  @Transactional
  public void triggerJob(JobKey jobKey, Map<String, String> args) throws SchedulerException {
    this.scheduler.triggerJob(jobKey, new JobDataMap(args));
  }

  @Transactional
  public void shutdown() throws SchedulerException {
    if (this.scheduler == null) {
      log.warn("scheduler is null");
      return;
    }
    this.scheduler.shutdown();
  }

  @Transactional
  public void clear() throws SchedulerException {
    this.scheduler.clear();
  }

  @Transactional
  public List<JobDetail> jobs() throws SchedulerException {
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
  public boolean isRunning(TriggerKey triggerKey) throws SchedulerException {
    List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
    return jobContexts.stream()
        .anyMatch(context -> triggerKey.equals(context.getTrigger().getKey()));
  }

  public ListenerManager getListenerManager() throws SchedulerException {
    return this.scheduler.getListenerManager();
  }
}
