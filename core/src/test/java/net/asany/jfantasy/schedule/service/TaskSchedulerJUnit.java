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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.asany.jfantasy.framework.util.common.DateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
public class TaskSchedulerJUnit {

  @Autowired private TaskScheduler scheduleService;

  @BeforeEach
  public void setUp() throws Exception {
    Map<String, String> data = new HashMap<String, String>();
    data.put("name", "limaofeng");
    data.put("time", DateUtil.format("yyyy-MM-dd HH:mm:ss"));
    // 添加 job
    scheduleService.addJob(
        ScheduleHelper.newJob(HelloJob.class, "junit", "test", data).build(), false);
    // 添加触发器
    Map<String, String> _data = new HashMap<String, String>();
    _data.put("name", "limaofeng-1");
    System.out.println("添加触发器:" + DateUtil.format("yyyy-MM-dd HH:mm:ss"));
    scheduleService.scheduleJob(
        ScheduleHelper.newTrigger(
                JobKey.jobKey("junit", "test"),
                TriggerKey.triggerKey("test"),
                ScheduleHelper.simple(TimeUnit.SECONDS.toMillis(10), 0))
            .build());

    /*
     * Date date = DateUtil.now(); String expression =
     * DateUtil.format(DateUtil.add(date,
     * Calendar.SECOND,5),"ss mm HH dd MM ? yyyy"); _data.put("title","五秒后触发");
     * scheduleService.addTrigger(JobKey.jobKey("junit",
     * "test"),TriggerKey.triggerKey("cron"),expression,_data);
     *
     * _data.put("title","立即触发"); scheduleService.triggerJob(JobKey.jobKey("junit",
     * "test"),_data);
     */
  }

  @AfterEach
  public void tearDown() throws Exception {
    // 删除 触发器
    scheduleService.removeTrigger(TriggerKey.triggerKey("test"));
    // 删除 job
    scheduleService.deleteJob(JobKey.jobKey("junit", "test"));
  }

  @Test
  public void testCron() {
    String expression = DateUtil.format("ss mm HH dd MM ? yyyy");
    assertTrue(CronExpression.isValidExpression(expression));
  }

  @Test
  public void run() throws InterruptedException {
    Thread.sleep(TimeUnit.SECONDS.toMillis(200));
  }

  @Test
  public void testIsStartTimerTisk() throws Exception {}

  @Test
  public void testIsShutDownTimerTisk() throws Exception {}

  @Test
  public void testPauseJob() throws Exception {}

  @Test
  public void testResumeJob() throws Exception {}

  @Test
  public void testDeleteJob() throws Exception {}

  @Test
  public void testPauseTrigger() throws Exception {}

  @Test
  public void testRemoveTrigdger() throws Exception {}

  @Test
  public void testPauseAll() throws Exception {}

  @Test
  public void testResumeAll() throws Exception {}

  @Test
  public void testInterrupt() throws Exception {}

  @Test
  public void testTriggerJob() throws Exception {}
}
