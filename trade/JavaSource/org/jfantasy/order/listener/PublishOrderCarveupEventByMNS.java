package org.jfantasy.order.listener;


import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.ms.EventEmitter;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.event.OrderCarveupEvent;
import org.jfantasy.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PublishOrderCarveupEventByMNS implements ApplicationListener<OrderCarveupEvent> {

    private OrderService orderService;
    private EventEmitter eventEmitter;

    @Override
    public void onApplicationEvent(OrderCarveupEvent event) {
        Order order = this.orderService.get(event.getOrderId());
        eventEmitter.fireEvent("order.carveup", order.getId(), String.format("订单[%s]已分配收益", order.getId()), JSON.serialize(order, "payments", "refunds", "payment_config"));
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
