package org.jfantasy.order.boot;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderType;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.job.OrderClose;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.order.service.OrderTypeService;
import org.jfantasy.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JobCheckForPayTimeoutCommandLineRunner implements CommandLineRunner {

    private ScheduleService scheduleService;
    private OrderTypeService orderTypeService;
    private OrderService orderService;

    @Override
    public void run(String... args) throws Exception {
        scheduleService.addJob(OrderClose.JOB_KEY, OrderClose.class);
        // 检查未正常运行的任务
        for (OrderType orderType : orderTypeService.getAll()) {
            for (Order order : orderService.find(Restrictions.eq("type", orderType.getId()), Restrictions.eq("status", OrderStatus.unpaid), Restrictions.lt("createTime", DateUtil.add(DateUtil.now(), Calendar.MINUTE, -30)))) {
                if (!this.scheduleService.checkExists(OrderClose.triggerKey(order))){
                    Map<String, String> data = new HashMap<>();
                    data.put("id", order.getId());
                    Date expireDate = DateUtil.add(order.getCreateTime(), Calendar.MINUTE, Math.toIntExact(orderType.getExpires()));
                    this.scheduleService.addTrigger(OrderClose.JOB_KEY, OrderClose.triggerKey(order), DateUtil.format(expireDate, "ss mm HH dd MM ? yyyy"), data);
                }
            }
        }
    }

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Autowired
    public void setOrderTypeService(OrderTypeService orderTypeService) {
        this.orderTypeService = orderTypeService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

}
