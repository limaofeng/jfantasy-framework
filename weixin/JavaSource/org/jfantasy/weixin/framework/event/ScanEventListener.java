package org.jfantasy.weixin.framework.event;

import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.content.Event;
import org.jfantasy.weixin.framework.session.WeixinSession;

/**
 * 用户已关注时的事件推送监听接口
 */
public interface ScanEventListener  extends WeixinEventListener {

    void onScan(WeixinSession session, Event event, EventMessage message);

}
