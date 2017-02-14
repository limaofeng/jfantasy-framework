package org.jfantasy.order.event;

/**
 * 订单完成事件
 */
public class OrderCompleteEvent extends OrderStatusEvent {

    public OrderCompleteEvent(String id) {
        super(id);
    }

}
