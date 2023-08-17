package org.jfantasy.schedule.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.StringMatcher;

/**
 * 定时任务管理
 *
 * @author limaofeng
 */
@Slf4j
public class TaskScheduler {

  private final Scheduler scheduler;

  public TaskScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public void scheduleTask(
      Class<? extends Job> jobClass, String jobName, String jobGroup, String cronExpression)
      throws SchedulerException {
    JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();

    CronTrigger trigger =
        TriggerBuilder.newTrigger()
            .withIdentity(jobName + "Trigger", jobGroup)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .build();

    scheduler.scheduleJob(jobDetail, trigger);
  }

  public void pauseTask(String jobName, String jobGroup) throws SchedulerException {
    JobKey jobKey = new JobKey(jobName, jobGroup);
    scheduler.pauseJob(jobKey);
  }

  public void resumeTask(String jobName, String jobGroup) throws SchedulerException {
    JobKey jobKey = new JobKey(jobName, jobGroup);
    scheduler.resumeJob(jobKey);
  }

  public void deleteTask(String jobName, String jobGroup) throws SchedulerException {
    JobKey jobKey = new JobKey(jobName, jobGroup);
    scheduler.deleteJob(jobKey);
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

  public List<String> getJobGroupNames() throws SchedulerException {
    return this.scheduler.getJobGroupNames();
  }

  public List<String> getTriggerGroupNames() throws SchedulerException {
    return this.scheduler.getTriggerGroupNames();
  }

  /**
   * 获取全部jobKey
   *
   * @return list<JobKey>
   */
  public List<JobKey> getJobKeys() throws SchedulerException {
    List<JobKey> jobKeys = new ArrayList<>();
    for (String group : this.scheduler.getJobGroupNames()) {
      jobKeys.addAll(
          this.scheduler.getJobKeys(
              new GroupMatcher<JobKey>(group, StringMatcher.StringOperatorName.EQUALS) {}));
    }
    return jobKeys;
  }

  public List<? extends Trigger> getTriggers(JobKey jobKey) throws SchedulerException {
    return this.scheduler.getTriggersOfJob(jobKey);
  }

  public List<TriggerKey> getTriggers() throws SchedulerException {
    List<TriggerKey> triggerKeys = new ArrayList<>();
    for (String group : this.scheduler.getTriggerGroupNames()) {
      triggerKeys.addAll(
          this.scheduler.getTriggerKeys(
              new GroupMatcher<TriggerKey>(group, StringMatcher.StringOperatorName.EQUALS) {}));
    }
    return triggerKeys;
  }

  public List<TriggerKey> getTriggers(GroupMatcher<TriggerKey> matcher) throws SchedulerException {
    return new ArrayList<>(this.scheduler.getTriggerKeys(matcher));
  }

  public JobDetail getJobDetail(JobKey jobKey) throws SchedulerException {
    return this.scheduler.getJobDetail(jobKey);
  }

  public boolean checkExists(JobKey jobKey) throws SchedulerException {
    return this.scheduler.checkExists(jobKey);
  }

  public boolean checkExists(TriggerKey triggerKey) throws SchedulerException {
    return this.scheduler.checkExists(triggerKey);
  }

  public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
    this.scheduler.scheduleJob(jobDetail, trigger);
  }

  public void scheduleJob(Trigger trigger) throws SchedulerException {
    this.scheduler.scheduleJob(trigger);
  }

  public void rescheduleJob(Trigger trigger) throws SchedulerException {
    this.scheduler.rescheduleJob(trigger.getKey(), trigger);
  }

  public void rescheduleJob(TriggerKey triggerKey, Trigger newTrigger) throws SchedulerException {
    scheduler.rescheduleJob(triggerKey, newTrigger);
  }

  public void addJob(JobDetail jobDetail, boolean replace) throws SchedulerException {
    this.scheduler.addJob(jobDetail, replace);
  }

  public Trigger.TriggerState getTriggerState(TriggerKey triggerKey) throws SchedulerException {
    return this.scheduler.getTriggerState(triggerKey);
  }

  /**
   * 是否启动
   *
   * @return boolean
   */
  public boolean isStarted() throws SchedulerException {
    return this.scheduler != null && this.scheduler.isStarted();
  }

  /**
   * 是否关闭
   *
   * @return boolean
   */
  public boolean isShutdown() throws SchedulerException {
    return this.scheduler != null && this.scheduler.isShutdown();
  }

  /**
   * 停止 job
   *
   * @param jobName 任务名称
   * @param groupName 组名称
   */
  public void pauseJob(String jobName, String groupName) throws SchedulerException {
    this.scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
  }

  /**
   * 恢复 job
   *
   * @param jobKey 任务名称
   */
  public void resumeJob(JobKey jobKey) throws SchedulerException {
    this.scheduler.resumeJob(jobKey);
  }

  /**
   * 删除指定的 job
   *
   * @param jobKey 任务名称
   * @return boolean
   */
  public boolean deleteJob(JobKey jobKey) throws SchedulerException {
    return this.scheduler.deleteJob(jobKey);
  }

  /**
   * 停止触发器
   *
   * @param triggerKey 触发器名称
   */
  public void pauseTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.pauseTrigger(triggerKey);
  }

  /**
   * 重启触发器
   *
   * @param triggerKey 触发器名称
   */
  public void resumeTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.resumeTrigger(triggerKey); // 重启触发器
  }

  /**
   * 移除触发器
   *
   * @param triggerKey 触发器名称
   * @return boolean
   */
  public boolean removeTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.pauseTrigger(triggerKey); // 停止触发器
    return this.scheduler.unscheduleJob(triggerKey); // 移除触发器
  }

  /** 暂停调度中所有的job任务 */
  public void pauseAll() throws SchedulerException {
    scheduler.pauseAll();
  }

  /** 恢复调度中所有的job的任务 */
  public void resumeAll() throws SchedulerException {
    scheduler.resumeAll();
  }

  /**
   * 中断TASK执行 job
   *
   * @param jobKey 触发器名称
   * @return boolean
   */
  public boolean interrupt(JobKey jobKey) throws UnableToInterruptJobException {
    return scheduler.interrupt(jobKey);
  }

  /**
   * 直接执行job
   *
   * @param jobKey JobKey
   */
  public void triggerJob(JobKey jobKey) throws SchedulerException {
    this.scheduler.triggerJob(jobKey);
  }

  /**
   * 直接触发job
   *
   * @param jobKey JobKey
   * @param args 执行参数
   */
  public void triggerJob(JobKey jobKey, Map<String, String> args) throws SchedulerException {
    this.scheduler.triggerJob(jobKey, new JobDataMap(args));
  }

  public void shutdown() throws SchedulerException {
    if (this.scheduler == null) {
      log.warn("scheduler is null");
      return;
    }
    this.scheduler.shutdown();
  }

  public void clear() throws SchedulerException {
    this.scheduler.clear();
  }

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

  public boolean isRunning(TriggerKey triggerKey) throws SchedulerException {
    List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
    return jobContexts.stream()
        .anyMatch(context -> triggerKey.equals(context.getTrigger().getKey()));
  }

  public ListenerManager getListenerManager() throws SchedulerException {
    return this.scheduler.getListenerManager();
  }
}
