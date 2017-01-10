package org.jfantasy.order.listener;

import org.jfantasy.order.bean.Order;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.event.OrderStatusChangedEvent;
import org.jfantasy.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderCashFlowListener implements ApplicationListener<OrderStatusChangedEvent> {

    private OrderService orderService;

    @Async
    @Override
    public void onApplicationEvent(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        if (order.getStatus() != OrderStatus.complete) {
            return;
        }
        orderService.cashflow(order.getId());
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

}
