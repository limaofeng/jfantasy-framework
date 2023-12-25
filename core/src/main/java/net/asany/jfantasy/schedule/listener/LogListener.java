package net.asany.jfantasy.schedule.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
public class LogListener implements SchedulerListener {

  @Override
  public void jobScheduled(Trigger trigger) {
    log.debug(trigger.toString());
  }

  @Override
  public void jobUnscheduled(TriggerKey triggerKey) {
    log.debug(triggerKey.toString());
  }

  @Override
  public void triggerFinalized(Trigger trigger) {
    log.debug(trigger.toString());
  }

  @Override
  public void triggerPaused(TriggerKey triggerKey) {
    log.debug(triggerKey.toString());
  }

  @Override
  public void triggersPaused(String s) {}

  @Override
  public void triggerResumed(TriggerKey triggerKey) {}

  @Override
  public void triggersResumed(String s) {}

  @Override
  public void jobAdded(JobDetail jobDetail) {}

  @Override
  public void jobDeleted(JobKey jobKey) {}

  @Override
  public void jobPaused(JobKey jobKey) {}

  @Override
  public void jobsPaused(String s) {}

  @Override
  public void jobResumed(JobKey jobKey) {}

  @Override
  public void jobsResumed(String s) {}

  @Override
  public void schedulerError(String s, SchedulerException e) {}

  @Override
  public void schedulerInStandbyMode() {}

  @Override
  public void schedulerStarted() {}

  @Override
  public void schedulerStarting() {}

  @Override
  public void schedulerShutdown() {}

  @Override
  public void schedulerShuttingdown() {}

  @Override
  public void schedulingDataCleared() {}
}
