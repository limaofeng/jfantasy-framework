package org.jfantasy.pay.event;

import org.jfantasy.pay.bean.Order;
import org.springframework.context.ApplicationEvent;

public class OrderStatusChangedEvent extends ApplicationEvent {

    public OrderStatusChangedEvent(Order source) {
        super(source);
    }

    public Order getOrder() {
        return (Order) this.getSource();
    }

}
