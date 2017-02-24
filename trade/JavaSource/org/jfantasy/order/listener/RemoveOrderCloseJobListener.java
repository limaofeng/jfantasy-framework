package org.jfantasy.order.listener;


import org.jfantasy.order.bean.Order;
import org.jfantasy.order.event.OrderPayEvent;
import org.jfantasy.order.job.OrderClose;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveOrderCloseJobListener implements ApplicationListener<OrderPayEvent> {

    private ScheduleService scheduleService;
    private OrderService orderService;

    @Override
    @Transactional
    public void onApplicationEvent(OrderPayEvent event) {
        Order order = this.orderService.get(event.getOrderId());
        this.scheduleService.removeTrigdger(OrderClose.triggerKey(order));
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

}
