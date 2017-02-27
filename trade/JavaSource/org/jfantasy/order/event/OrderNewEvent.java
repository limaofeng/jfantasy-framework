package org.jfantasy.order.event;

/**
 * 新订单
 */
public class OrderNewEvent extends OrderStatusEvent {

    public OrderNewEvent(String id) {
        super(id);
    }

}
