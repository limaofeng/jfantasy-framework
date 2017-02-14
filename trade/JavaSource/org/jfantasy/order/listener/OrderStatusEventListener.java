package org.jfantasy.order.listener;

import org.jfantasy.order.bean.Order;
import org.jfantasy.order.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusEventListener implements ApplicationListener<OrderStatusChangedEvent> {

    private ApplicationContext applicationContext;

    @Async
    @Override
    public void onApplicationEvent(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        switch (order.getStatus()) {
            case closed:
                this.applicationContext.publishEvent(new OrderCloseEvent(order.getId()));
                break;
            case complete:
                this.applicationContext.publishEvent(new OrderCompleteEvent(order.getId()));
                break;
            case paid:
                this.applicationContext.publishEvent(new OrderPayEvent(order.getId()));
                break;
            case refunded:
                this.applicationContext.publishEvent(new OrderRefundEvent(order.getId()));
                break;
            case refunding:
                this.applicationContext.publishEvent(new OrderRefundingEvent(order.getId()));
                break;
            case unpaid:
                this.applicationContext.publishEvent(new OrderNewEvent(order.getId()));
                break;
            default:
        }
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
