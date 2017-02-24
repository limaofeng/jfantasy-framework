package org.jfantasy.order.event;

/**
 * 订单完成事件
 */
public class OrderArchiveEvent extends OrderFlowEvent {

    public OrderArchiveEvent(String id) {
        super(id);
    }

}
