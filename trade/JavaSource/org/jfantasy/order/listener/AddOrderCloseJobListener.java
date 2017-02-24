package org.jfantasy.order.listener;


import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderType;
import org.jfantasy.order.event.OrderNewEvent;
import org.jfantasy.order.job.OrderClose;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.order.service.OrderTypeService;
import org.jfantasy.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AddOrderCloseJobListener implements ApplicationListener<OrderNewEvent> {

    private ScheduleService scheduleService;
    private OrderService orderService;
    private OrderTypeService orderTypeService;

    @Override
    @Transactional
    public void onApplicationEvent(OrderNewEvent event) {
        Order order = this.orderService.get(event.getOrderId());
        OrderType orderType = orderTypeService.get(order.getType());
        Map<String, String> data = new HashMap<>();
        data.put("id", order.getId());
        Date expireDate = DateUtil.add(order.getCreateTime(), Calendar.MINUTE, Math.toIntExact(orderType.getExpires()));
        this.scheduleService.addTrigger(OrderClose.JOB_KEY, OrderClose.triggerKey(order), DateUtil.format(expireDate, "ss mm HH dd MM ? yyyy"), data);
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setOrderTypeService(OrderTypeService orderTypeService) {
        this.orderTypeService = orderTypeService;
    }

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

}
