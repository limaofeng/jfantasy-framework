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

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public enum TriggerType {
  simple("简单规则"),
  cron("cron表达式");

  private final String value;

  TriggerType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public Trigger newTrigger(String name, String group, int rate, Integer times) {
    return TriggerBuilder.newTrigger()
        .withIdentity(name, group)
        .withSchedule(
            simpleSchedule()
                .withIntervalInMinutes(rate)
                .withRepeatCount(times)
                .withMisfireHandlingInstructionFireNow())
        .build();
  }

  public Trigger newTrigger(String name, String group, String cronExpression) {
    return TriggerBuilder.newTrigger()
        .withIdentity(name, group)
        .withSchedule(cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  public static TriggerType getType(Trigger trigger) {
    if (trigger instanceof CronTriggerImpl) {
      return TriggerType.cron;
    } else if (trigger instanceof SimpleTriggerImpl) {
      return TriggerType.simple;
    }
    return null;
  }
}
