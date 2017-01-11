package org.jfantasy.weixin.framework.message;


import org.jfantasy.weixin.framework.message.content.Event;

import java.util.Date;

public class DefalutEventMessage extends AbstractWeixinMessage<Event> implements EventMessage<Event> {

    public DefalutEventMessage(Long id, String fromUserName, Date createTime) {
        super(id, fromUserName, createTime);
    }

    @Override
    public EventType getEventType() {
        return this.getContent().getType();
    }

}
