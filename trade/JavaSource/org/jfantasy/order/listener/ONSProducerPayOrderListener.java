package org.jfantasy.order.listener;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.ms.EventEmitter;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.event.OrderStatusChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ONSProducerPayOrderListener implements ApplicationListener<OrderStatusChangedEvent> {

    private EventEmitter eventEmitter;

    @Override
    public void onApplicationEvent(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        eventEmitter.fireEvent("order.paid",order.getId(),String.format("订单[%s]支付成功",order.getId()),JSON.serialize(order, "payments", "refunds", "payment_config"));
    }

    @Autowired
    public void setEventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

}
