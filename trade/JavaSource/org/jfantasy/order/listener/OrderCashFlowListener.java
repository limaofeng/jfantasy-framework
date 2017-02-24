package org.jfantasy.order.listener;

import org.jfantasy.order.event.OrderCompleteEvent;
import org.jfantasy.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderCashFlowListener implements ApplicationListener<OrderCompleteEvent> {

    private OrderService orderService;

    @Async
    @Override
    public void onApplicationEvent(OrderCompleteEvent event) {
        orderService.cashflow(event.getOrderId());
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

}
