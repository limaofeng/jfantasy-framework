package org.jfantasy.order;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.order.entity.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;

public class PayMessageListener implements MessageListener {

    private OrderMessageListener orderMessageListener;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        if ("order".equals(message.getTag())) {
            OrderDTO details = JSON.deserialize(new String(message.getBody()), OrderDTO.class);
            assert details != null;
            orderMessageListener.on(details.getId(), details.getStatus(), details);
        }
        return Action.CommitMessage;
    }

    @Autowired
    public void setOrderMessageListener(OrderMessageListener orderMessageListener) {
        this.orderMessageListener = orderMessageListener;
    }

}
