package org.jfantasy.order.event;

import org.jfantasy.order.bean.Order;
import org.springframework.context.ApplicationEvent;

public class OrderStatusChangedEvent extends ApplicationEvent {

    public OrderStatusChangedEvent(Order source) {
        super(source);
    }

    public Order getOrder() {
        return (Order) this.getSource();
    }

}
