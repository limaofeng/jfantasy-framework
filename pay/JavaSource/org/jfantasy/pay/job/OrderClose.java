package org.jfantasy.pay.job;


import org.jfantasy.pay.order.entity.OrderKey;
import org.jfantasy.pay.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

public class OrderClose implements Job {

    @Resource
    private OrderService orderService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        OrderKey key = OrderKey.newInstance(context.getMergedJobDataMap().getString("id"));
        orderService.close(key);
    }

}
