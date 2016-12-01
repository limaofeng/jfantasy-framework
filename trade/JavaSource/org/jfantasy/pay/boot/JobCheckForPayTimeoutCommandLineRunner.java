package org.jfantasy.pay.boot;

import org.jfantasy.order.job.OrderClose;
import org.jfantasy.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class JobCheckForPayTimeoutCommandLineRunner implements CommandLineRunner {

    private ScheduleService scheduleService;

    @Override
    public void run(String... args) throws Exception {
        scheduleService.addJob(OrderClose.JOB_KEY, OrderClose.class);
        // 检查未正常运行的任务
    }

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

}
