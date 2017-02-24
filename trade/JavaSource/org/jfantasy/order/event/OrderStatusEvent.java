package org.jfantasy.order.event;

import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.order.bean.Order;
import org.springframework.context.ApplicationEvent;

public abstract class OrderStatusEvent extends ApplicationEvent {

    OrderStatusEvent(String id) {
        super(id);
    }

    public String getOrderId() {
        return this.getSource().toString();
    }

    public static OrderStatusEvent newInstance(Order order) {
        switch (order.getStatus()) {
            case closed:
                return new OrderCloseEvent(order.getId());
            case complete:
                return new OrderCompleteEvent(order.getId());
            case paid:
                return new OrderPayEvent(order.getId());
            case refunded:
                return new OrderRefundEvent(order.getId());
            case refunding:
                return new OrderRefundingEvent(order.getId());
            case unpaid:
                return new OrderNewEvent(order.getId());
            default:
                throw new IgnoreException("怎么可能出现这个BUG");
        }
    }
}
