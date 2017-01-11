package org.jfantasy.weixin.framework.event;

import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.content.EventLocation;
import org.jfantasy.weixin.framework.session.WeixinSession;

/**
 * 微信事件消息监听接口
 */
public interface LocationEventListener extends WeixinEventListener {

    void onLocation(WeixinSession session, EventLocation event, EventMessage message);

}
