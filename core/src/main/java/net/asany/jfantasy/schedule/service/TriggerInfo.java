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
package net.asany.jfantasy.schedule.service;

import java.util.Date;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class TriggerInfo {

  private Trigger.TriggerState state;
  private Date preFire;
  private Date nextFire;
  private String cronExpression;
  private long repeatInterval;
  private TriggerType type;

  private int repeatCount;

  public TriggerInfo(Trigger trigger) {
    this.preFire = trigger.getPreviousFireTime();
    this.nextFire = trigger.getNextFireTime();
    this.type = TriggerType.getType(trigger);
    if (TriggerType.cron == this.type) {
      this.cronExpression = ((CronTriggerImpl) trigger).getCronExpression();
    } else if (TriggerType.simple == this.type) {
      this.repeatInterval = ((SimpleTriggerImpl) trigger).getRepeatInterval();
      this.repeatCount = ((SimpleTriggerImpl) trigger).getRepeatCount();
    }
  }

  public Trigger.TriggerState getState() {
    return state;
  }

  public void setState(Trigger.TriggerState state) {
    this.state = state;
  }

  public Date getPreFire() {
    return preFire;
  }

  public void setPreFire(Date preFire) {
    this.preFire = preFire;
  }

  public Date getNextFire() {
    return nextFire;
  }

  public void setNextFire(Date nextFire) {
    this.nextFire = nextFire;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public long getRepeatInterval() {
    return repeatInterval;
  }

  public void setRepeatInterval(long repeatInterval) {
    this.repeatInterval = repeatInterval;
  }

  public TriggerType getType() {
    return type;
  }

  public void setType(TriggerType type) {
    this.type = type;
  }

  public int getRepeatCount() {
    return repeatCount;
  }

  public void setRepeatCount(int repeatCount) {
    this.repeatCount = repeatCount;
  }
}
