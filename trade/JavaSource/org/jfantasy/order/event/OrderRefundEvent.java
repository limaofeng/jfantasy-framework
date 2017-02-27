package org.jfantasy.order.event;

/**
 * 订单退款事件
 */
public class OrderRefundEvent extends OrderStatusEvent {

    public OrderRefundEvent(String id) {
        super(id);
    }

}
