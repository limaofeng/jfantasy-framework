package org.jfantasy.weixin.framework.event;

import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.content.Event;
import org.jfantasy.weixin.framework.session.WeixinSession;

/**
 * 取消订阅事件消息监听接口
 */
public interface UnsubscribeEventListener  extends WeixinEventListener {

    void onUnsubscribe(WeixinSession session, Event event, EventMessage message);

}
