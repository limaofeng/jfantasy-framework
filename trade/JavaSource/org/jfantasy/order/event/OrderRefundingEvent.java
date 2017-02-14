package org.jfantasy.order.event;

/**
 * 订单完成事件
 */
public class OrderRefundingEvent extends OrderStatusEvent {

    public OrderRefundingEvent(String id) {
        super(id);
    }

}
