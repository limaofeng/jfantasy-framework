package org.jfantasy.order.event;

/**
 * 订单检查
 */
public class OrderCheckedEvent extends OrderFlowEvent {

    public OrderCheckedEvent(String id) {
        super(id);
    }

}
