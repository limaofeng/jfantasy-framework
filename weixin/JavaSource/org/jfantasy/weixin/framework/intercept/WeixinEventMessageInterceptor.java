package org.jfantasy.weixin.framework.intercept;

import org.jfantasy.weixin.framework.event.EventListenerAdapter;
import org.jfantasy.weixin.framework.event.WeixinEventListener;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.message.content.Event;
import org.jfantasy.weixin.framework.session.WeixinSession;

import java.util.List;

public class WeixinEventMessageInterceptor implements WeixinMessageInterceptor {

    private EventListenerAdapter adapter;


    public WeixinEventMessageInterceptor(EventMessage.EventType eventType, List<WeixinEventListener> listeners) {
        this.adapter = new EventListenerAdapter(eventType, listeners);
    }

    @Override
    public WeixinMessage intercept(WeixinSession session, WeixinMessage message, Invocation invocation) throws WeixinException {
        try {
            return invocation.invoke();
        } finally {
            adapter.execute(session, (Event) message.getContent(), (EventMessage) message);
        }
    }

}
