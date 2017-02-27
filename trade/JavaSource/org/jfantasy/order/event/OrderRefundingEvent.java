package org.jfantasy.order.event;

/**
 * 订单发起退款事件
 */
public class OrderRefundingEvent extends OrderStatusEvent {

    public OrderRefundingEvent(String id) {
        super(id);
    }

}
