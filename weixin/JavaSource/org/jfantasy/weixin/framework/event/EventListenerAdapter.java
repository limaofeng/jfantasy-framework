package org.jfantasy.weixin.framework.event;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.content.Event;
import org.jfantasy.weixin.framework.message.content.EventLocation;
import org.jfantasy.weixin.framework.session.WeixinSession;

import java.util.ArrayList;
import java.util.List;

public class EventListenerAdapter {

    private static final Log LOG = LogFactory.getLog(EventListenerAdapter.class);

    private List<WeixinEventListener> listeners;
    private EventMessage.EventType eventType;
    private List<EventListenerAdapter> adapters = new ArrayList<EventListenerAdapter>();

    public EventListenerAdapter(EventMessage.EventType eventType, List<WeixinEventListener> listeners) {
        this.eventType = eventType;
    }


    public void execute(WeixinSession session, Event content, EventMessage message) {
        for (WeixinEventListener listener : listeners) {
            if (listener instanceof ClickEventListener && eventType == EventMessage.EventType.CLICK) {
                ((ClickEventListener) listener).onClick(session, content, message);
            } else if (listener instanceof LocationEventListener && eventType == EventMessage.EventType.location) {
                ((LocationEventListener) listener).onLocation(session, (EventLocation) content, message);
            } else if (listener instanceof ScanEventListener && eventType == EventMessage.EventType.SCAN) {
                ((ScanEventListener) listener).onScan(session, content, message);
            } else if (listener instanceof SubscribeEventListener && eventType == EventMessage.EventType.subscribe) {
                ((SubscribeEventListener) listener).onSubscribe(session, content, message);
            } else if (listener instanceof UnsubscribeEventListener && eventType == EventMessage.EventType.unsubscribe) {
                ((UnsubscribeEventListener) listener).onUnsubscribe(session, content, message);
            } else if (listener instanceof ViewEventListener && eventType == EventMessage.EventType.VIEW) {
                ((ViewEventListener) listener).onView(session, content, message);
            } else {
                LOG.error("监听器的与监听类型不匹配:" + eventType + "=" + listener);
            }
        }
    }

}
