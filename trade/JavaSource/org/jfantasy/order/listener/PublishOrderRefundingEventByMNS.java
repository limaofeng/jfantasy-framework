package org.jfantasy.order.listener;


import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.ms.EventEmitter;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.event.OrderRefundingEvent;
import org.jfantasy.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PublishOrderRefundingEventByMNS implements ApplicationListener<OrderRefundingEvent> {

    private OrderService orderService;
    private EventEmitter eventEmitter;

    @Override
    public void onApplicationEvent(OrderRefundingEvent event) {
        Order order = this.orderService.get(event.getOrderId());
        eventEmitter.fireEvent("order.refunding", order.getId(), String.format("订单[%s]申请退款", order.getId()), JSON.serialize(order, "payments", "refunds", "payment_config"));
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setEventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }
}