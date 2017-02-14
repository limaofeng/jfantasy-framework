package org.jfantasy.order.event;

/**
 * 订单完成事件
 */
public class OrderPayEvent extends OrderStatusEvent {

    public OrderPayEvent(String id) {
        super(id);
    }

}
