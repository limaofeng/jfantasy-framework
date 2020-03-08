package org.jfantasy.schedule.service;

import org.jfantasy.framework.util.common.DateUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.CronExpression;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:backup/testconfig/spring/applicationContext.xml"})
public class ScheduleServiceJUnit {

    @Autowired
    private ScheduleService scheduleService;

    @Before
    public void setUp() throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", "limaofeng");
        data.put("time", DateUtil.format("yyyy-MM-dd HH:mm:ss"));
        // 添加 job
        scheduleService.addJob(JobKey.jobKey("junit", "test"), HelloJob.class,data);
        // 添加触发器
        Map<String, String> _data = new HashMap<String, String>();
        _data.put("name", "limaofeng-1");
        System.out.println("添加触发器:" + DateUtil.format("yyyy-MM-dd HH:mm:ss"));
        scheduleService.addTrigger(JobKey.jobKey("junit", "test"), TriggerKey.triggerKey("test"), TimeUnit.SECONDS.toMillis(10), 0, _data);

        /*
        Date date = DateUtil.now();
        String expression = DateUtil.format(DateUtil.add(date, Calendar.SECOND,5),"ss mm HH dd MM ? yyyy");
        _data.put("title","五秒后触发");
        scheduleService.addTrigger(JobKey.jobKey("junit", "test"),TriggerKey.triggerKey("cron"),expression,_data);

        _data.put("title","立即触发");
        scheduleService.triggerJob(JobKey.jobKey("junit", "test"),_data);
        */
    }

    @After
    public void tearDown() throws Exception {
        // 删除 触发器
        scheduleService.removeTrigdger(TriggerKey.triggerKey("test"));
        // 删除 job
        scheduleService.deleteJob(JobKey.jobKey("junit", "test"));
    }

    @Test
    public void testCron() {
        String expression = DateUtil.format("ss mm HH dd MM ? yyyy");
        Assert.assertTrue(CronExpression.isValidExpression(expression));
    }

    @Test
    public void run() throws InterruptedException {
        Thread.sleep(TimeUnit.SECONDS.toMillis(200));
    }

    @Test
    public void testIsStartTimerTisk() throws Exception {

    }

    @Test
    public void testIsShutDownTimerTisk() throws Exception {

    }

    @Test
    public void testPauseJob() throws Exception {

    }

    @Test
    public void testResumeJob() throws Exception {

    }

    @Test
    public void testDeleteJob() throws Exception {

    }

    @Test
    public void testPauseTrigger() throws Exception {

    }

    @Test
    public void testRemoveTrigdger() throws Exception {

    }

    @Test
    public void testPauseAll() throws Exception {

    }

    @Test
    public void testResumeAll() throws Exception {

    }

    @Test
    public void testInterrupt() throws Exception {

    }

    @Test
    public void testTriggerJob() throws Exception {

    }

}