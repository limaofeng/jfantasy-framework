package org.jfantasy.weixin.framework.event;

import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.content.Event;
import org.jfantasy.weixin.framework.session.WeixinSession;

/**
 * 点击菜单跳转链接时的事件推送监听接口
 */
public interface ViewEventListener extends WeixinEventListener {

    void onView(WeixinSession session, Event event, EventMessage message);

}
