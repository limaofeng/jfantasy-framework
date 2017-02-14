package org.jfantasy.order.event;

/**
 * 订单完成事件
 */
public class OrderCloseEvent extends OrderStatusEvent {

    public OrderCloseEvent(String id) {
        super(id);
    }

}
