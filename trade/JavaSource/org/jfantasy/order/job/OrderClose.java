package org.jfantasy.order.job;


import org.jfantasy.order.bean.Order;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.pay.error.PayException;
import org.quartz.*;

import javax.annotation.Resource;

public class OrderClose implements Job {

    public static final JobKey JOB_KEY = JobKey.jobKey("ORDER_CLOSE");

    @Resource
    private OrderService orderService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String id = context.getMergedJobDataMap().getString("id");
        try {
            orderService.close(id);
        } catch (PayException e) {
            e.printStackTrace();
        }
    }

    public static TriggerKey triggerKey(Order entity) {
        return TriggerKey.triggerKey(entity.getId(),"ORDER_CLOSE");
    }

}
