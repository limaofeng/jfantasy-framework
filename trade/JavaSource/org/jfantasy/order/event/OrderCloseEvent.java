package org.jfantasy.order.event;

/**
 * 订单关闭事件
 */
public class OrderCloseEvent extends OrderStatusEvent {

    public OrderCloseEvent(String id) {
        super(id);
    }

}
