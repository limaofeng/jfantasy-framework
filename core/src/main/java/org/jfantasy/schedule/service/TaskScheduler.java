package org.jfantasy.schedule.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.StringMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  @Transactional(rollbackFor = SchedulerException.class)
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

  @Transactional(rollbackFor = SchedulerException.class)
  public void scheduleTask(JobKey jobKey, TriggerKey triggerKey, Map<String, String> data) throws SchedulerException {
    Trigger trigger = ScheduleHelper.newTrigger(jobKey, triggerKey, data).build();
    this.scheduler.scheduleJob(trigger);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void pauseTask(String jobName, String jobGroup) throws SchedulerException {
    JobKey jobKey = new JobKey(jobName, jobGroup);
    scheduler.pauseJob(jobKey);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void resumeTask(String jobName, String jobGroup) throws SchedulerException {
    JobKey jobKey = new JobKey(jobName, jobGroup);
    scheduler.resumeJob(jobKey);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void deleteTask(String jobName, String jobGroup) throws SchedulerException {
    JobKey jobKey = new JobKey(jobName, jobGroup);
    scheduler.deleteJob(jobKey);
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

  @Transactional(rollbackFor = SchedulerException.class)
  public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
    this.scheduler.scheduleJob(jobDetail, trigger);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void scheduleJob(Trigger trigger) throws SchedulerException {
    this.scheduler.scheduleJob(trigger);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void rescheduleJob(Trigger trigger) throws SchedulerException {
    this.scheduler.rescheduleJob(trigger.getKey(), trigger);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void rescheduleJob(TriggerKey triggerKey, Trigger newTrigger) throws SchedulerException {
    scheduler.rescheduleJob(triggerKey, newTrigger);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void addJob(JobDetail jobDetail, boolean replace) throws SchedulerException {
    this.scheduler.addJob(jobDetail, replace);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void addJob(JobKey jobKey, Class<? extends Job> jobClass) throws SchedulerException {
    this.scheduler.addJob(ScheduleHelper.newJob(jobClass, jobKey.getName(), jobKey.getGroup()).build(), false);
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void addJob(JobKey jobKey, Class<? extends Job> jobClass, boolean replace) throws SchedulerException {
    this.scheduler.addJob(ScheduleHelper.newJob(jobClass, jobKey.getName(), jobKey.getGroup()).build(), replace);
  }

  @Transactional(readOnly = true)
  public Trigger.TriggerState getTriggerState(TriggerKey triggerKey) throws SchedulerException {
    return this.scheduler.getTriggerState(triggerKey);
  }

  /**
   * 是否启动
   *
   * @return boolean
   */
  @Transactional(readOnly = true)
  public boolean isStarted() throws SchedulerException {
    return this.scheduler != null && this.scheduler.isStarted();
  }

  /**
   * 是否关闭
   *
   * @return boolean
   */
  @Transactional(readOnly = true)
  public boolean isShutdown() throws SchedulerException {
    return this.scheduler != null && this.scheduler.isShutdown();
  }

  /**
   * 停止 job
   *
   * @param jobName 任务名称
   * @param groupName 组名称
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public void pauseJob(String jobName, String groupName) throws SchedulerException {
    this.scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
  }

  /**
   * 恢复 job
   *
   * @param jobKey 任务名称
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public void resumeJob(JobKey jobKey) throws SchedulerException {
    this.scheduler.resumeJob(jobKey);
  }

  /**
   * 删除指定的 job
   *
   * @param jobKey 任务名称
   * @return boolean
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public boolean deleteJob(JobKey jobKey) throws SchedulerException {
    return this.scheduler.deleteJob(jobKey);
  }

  /**
   * 停止触发器
   *
   * @param triggerKey 触发器名称
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public void pauseTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.pauseTrigger(triggerKey);
  }

  /**
   * 重启触发器
   *
   * @param triggerKey 触发器名称
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public void resumeTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.resumeTrigger(triggerKey); // 重启触发器
  }

  /**
   * 移除触发器
   *
   * @param triggerKey 触发器名称
   * @return boolean
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public boolean removeTrigger(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.pauseTrigger(triggerKey); // 停止触发器
    return this.scheduler.unscheduleJob(triggerKey); // 移除触发器
  }

  /** 暂停调度中所有的job任务 */
  @Transactional(rollbackFor = SchedulerException.class)
  public void pauseAll() throws SchedulerException {
    scheduler.pauseAll();
  }

  /** 恢复调度中所有的job的任务 */
  @Transactional(rollbackFor = SchedulerException.class)
  public void resumeAll() throws SchedulerException {
    scheduler.resumeAll();
  }

  /**
   * 中断TASK执行 job
   *
   * @param jobKey 触发器名称
   * @return boolean
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public boolean interrupt(JobKey jobKey) throws UnableToInterruptJobException {
    return scheduler.interrupt(jobKey);
  }

  /**
   * 直接执行job
   *
   * @param jobKey JobKey
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public void triggerJob(JobKey jobKey) throws SchedulerException {
    this.scheduler.triggerJob(jobKey);
  }

  /**
   * 直接触发job
   *
   * @param jobKey JobKey
   * @param args 执行参数
   */
  @Transactional(rollbackFor = SchedulerException.class)
  public void triggerJob(JobKey jobKey, Map<String, String> args) throws SchedulerException {
    this.scheduler.triggerJob(jobKey, new JobDataMap(args));
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void shutdown() throws SchedulerException {
    if (this.scheduler == null) {
      log.warn("scheduler is null");
      return;
    }
    this.scheduler.shutdown();
  }

  @Transactional(rollbackFor = SchedulerException.class)
  public void clear() throws SchedulerException {
    this.scheduler.clear();
  }

  @Transactional(rollbackFor = SchedulerException.class)
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

  @Transactional(readOnly = true)
  public boolean isRunning(TriggerKey triggerKey) throws SchedulerException {
    List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
    return jobContexts.stream()
        .anyMatch(context -> triggerKey.equals(context.getTrigger().getKey()));
  }

  public ListenerManager getListenerManager() throws SchedulerException {
    return this.scheduler.getListenerManager();
  }
}
