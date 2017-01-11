package org.jfantasy.weixin.framework.event;

import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.content.Event;
import org.jfantasy.weixin.framework.session.WeixinSession;

/**
 * 订阅事件消息监听接口
 */
public interface SubscribeEventListener  extends WeixinEventListener {

    void onSubscribe(WeixinSession session, Event event, EventMessage message);

}
