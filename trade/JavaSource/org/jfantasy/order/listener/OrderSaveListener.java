package org.jfantasy.order.listener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.jfantasy.framework.jackson.JSON;

public class OrderSaveListener implements MessageListener {

    @Override
    public Action consume(Message message, ConsumeContext context) {

        JSON.serialize(new String(message.getBody()));

        return null;
    }

}
