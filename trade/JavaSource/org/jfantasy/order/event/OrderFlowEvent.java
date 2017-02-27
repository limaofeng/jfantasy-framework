package org.jfantasy.order.event;

import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.order.bean.Order;

/**
 * 订单处理流程变更事件
 */
public class OrderFlowEvent extends OrderStatusEvent {

    public OrderFlowEvent(String id) {
        super(id);
    }

    public static OrderFlowEvent newInstance(Order order) {
        switch (order.getFlow()) {
            case carveup:
                return new OrderCarveupEvent(order.getId());
            case archive:
                return new OrderArchiveEvent(order.getId());
            case initial:
                return new OrderInitialEvent(order.getId());
            case checked:
                return new OrderCheckedEvent(order.getId());
            default:
                throw new IgnoreException("怎么可能出现这个BUG");
        }
    }

}
