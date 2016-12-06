package org.jfantasy.order.event;

import org.springframework.context.ApplicationEvent;

/**
 * 订单完成事件
 */
public class OrderCompleteEvent extends ApplicationEvent {

    public OrderCompleteEvent(String id) {
        super(id);
    }

    public String getOrderId() {
        return this.getSource().toString();
    }

}
