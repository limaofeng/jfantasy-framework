package org.jfantasy.order.event;

/**
 * 订单完成事件
 */
public class OrderCheckedEvent extends OrderFlowEvent {

    public OrderCheckedEvent(String id) {
        super(id);
    }

}
