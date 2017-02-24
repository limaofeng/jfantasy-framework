package org.jfantasy.order.event;

/**
 * 订单完成事件
 */
public class OrderInitialEvent extends OrderFlowEvent {

    public OrderInitialEvent(String id) {
        super(id);
    }

}
