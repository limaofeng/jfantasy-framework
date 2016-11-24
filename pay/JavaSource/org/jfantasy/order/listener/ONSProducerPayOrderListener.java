package org.jfantasy.order.listener;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import org.jfantasy.aliyun.AliyunSettings;
import org.jfantasy.autoconfigure.TradeAutoConfiguration;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.event.OrderStatusChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ONSProducerPayOrderListener implements ApplicationListener<OrderStatusChangedEvent> {

    @Resource(name = "pay.aliyunSettings")
    private AliyunSettings aliyunSettings;

    private final Producer producer;

    @Autowired
    public ONSProducerPayOrderListener(Producer producer) {
        this.producer = producer;
    }

    @Override
    public void onApplicationEvent(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        Message msg = new Message(aliyunSettings.getTopicId(), TradeAutoConfiguration.ONS_TAGS_ORDER, order.getId(), JSON.serialize(order,"payments","refunds").getBytes());
        producer.send(msg);
    }

}
