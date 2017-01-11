package org.jfantasy.weixin.listener;


import org.jfantasy.weixin.event.WeixinEventMessageEvent;
import org.jfantasy.weixin.framework.message.EventMessage;
import org.springframework.context.ApplicationListener;

public abstract class WeixinEventMessageApplicationListener implements ApplicationListener<WeixinEventMessageEvent> {

    abstract boolean supportsEventType(EventMessage.EventType eventType);

    @Override
    public void onApplicationEvent(WeixinEventMessageEvent event) {
        EventMessage message = (EventMessage) event.getSource();
        if (supportsEventType(message.getEventType())) {
            this.onEventMessage(message);
        }
    }

    abstract void onEventMessage(EventMessage message);

}
