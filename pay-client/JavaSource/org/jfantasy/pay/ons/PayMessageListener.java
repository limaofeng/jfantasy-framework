package org.jfantasy.pay.ons;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.pay.order.OrderService;
import org.jfantasy.pay.order.entity.OrderDetails;
import org.jfantasy.pay.order.entity.OrderKey;
import org.springframework.beans.factory.annotation.Autowired;

public class PayMessageListener implements MessageListener {

    @Autowired
    private OrderService orderService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        if ("order".equals(message.getTag())) {
            OrderDetails details = JSON.deserialize(new String(message.getBody()), OrderDetails.class);
            assert details != null;
            orderService.on(OrderKey.newInstance(details.getType(), details.getSn()), details.getStatus(), details);
        }
        return Action.CommitMessage;
    }

}
