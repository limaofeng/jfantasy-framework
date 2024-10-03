/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
