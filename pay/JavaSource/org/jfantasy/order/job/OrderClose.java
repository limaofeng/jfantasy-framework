package org.jfantasy.order.job;


import org.jfantasy.order.bean.Order;
import org.jfantasy.order.entity.OrderKey;
import org.jfantasy.order.service.OrderService;
import org.quartz.*;

import javax.annotation.Resource;

public class OrderClose implements Job {

    public static final JobKey JOB_KEY = JobKey.jobKey("ORDER_CLOSE");

    @Resource
    private OrderService orderService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        OrderKey key = OrderKey.newInstance(context.getMergedJobDataMap().getString("id"));
        orderService.close(key);
    }

    public static TriggerKey triggerKey(Order entity) {
        return TriggerKey.triggerKey(entity.getKey(),"ORDER_CLOSE");
    }

}
