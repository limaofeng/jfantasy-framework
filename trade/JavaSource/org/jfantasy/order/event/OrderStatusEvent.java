package org.jfantasy.order.event;

import org.springframework.context.ApplicationEvent;

public abstract class OrderStatusEvent extends ApplicationEvent {

    OrderStatusEvent(String id) {
        super(id);
    }

    public String getOrderId() {
        return this.getSource().toString();
    }
}
