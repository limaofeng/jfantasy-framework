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
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.DateUtil;
import org.quartz.*;

@Slf4j
public class HelloJob implements Job {

  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap data = context.getMergedJobDataMap();
    StringBuilder str = new StringBuilder();

    str.append("触发时间:").append(DateUtil.format("yyyy-MM-dd HH:mm:ss"));
    str.append("\n\n-===============Hello World! - ")
        .append(new Date())
        .append("==================-")
        .append("\n");
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      str.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
    }
    str.append("-===============打印详细的job信息==================-").append("\n");
    print(context.getJobDetail(), context.getTrigger(), str);
    log.debug(str.toString());
  }

  public void print(JobDetail jobDetail, Trigger trigger, StringBuilder log) {
    JobKey jobKey = jobDetail.getKey();
    log.append("jobKey:\t")
        .append(jobKey.getGroup())
        .append(".")
        .append(jobKey.getName())
        .append("\n");
    log.append("jobClass:\t").append(jobDetail.getJobClass()).append("\n");
    log.append("jobDataMap:").append("\n");
    for (Map.Entry<String, Object> entry : jobDetail.getJobDataMap().entrySet()) {
      log.append("\t").append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
    }
    log.append("trigger:").append(trigger.getKey()).append("\n");
    log.append("previousFireTime:")
        .append(DateUtil.format(trigger.getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss"))
        .append("\n");
    log.append("nextFireTime:")
        .append(DateUtil.format(trigger.getNextFireTime(), "yyyy-MM-dd HH:mm:ss"))
        .append("\n");
    log.append("EndTime:")
        .append(DateUtil.format(trigger.getEndTime(), "yyyy-MM-dd HH:mm:ss"))
        .append("\n");
    log.append("jobDataMap:").append("\n");
    for (Map.Entry<String, Object> entry : trigger.getJobDataMap().entrySet()) {
      log.append("\t").append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
    }
    if (trigger instanceof CronTrigger) {
      log.append("cron:").append(((CronTrigger) trigger).getCronExpression()).append("\n");
    } else if (trigger instanceof SimpleTrigger) {
      SimpleTrigger simpleTrigger = ((SimpleTrigger) trigger);
      log.append("repeatInterval:").append(simpleTrigger.getRepeatInterval()).append("\n");
      log.append("repeatCount:").append(simpleTrigger.getRepeatCount()).append("\n");
      log.append("timesTriggered:").append(simpleTrigger.getTimesTriggered()).append("\n");
    }
  }
}
