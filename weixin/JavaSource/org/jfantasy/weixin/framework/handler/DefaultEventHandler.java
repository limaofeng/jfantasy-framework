package org.jfantasy.weixin.framework.handler;

import org.jfantasy.weixin.framework.message.EmptyMessage;
import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.WeixinSession;

public class DefaultEventHandler extends EventWeixinHandler {

    @Override
    protected WeixinMessage handleEventMessage(WeixinSession session, EventMessage message) {
        return EmptyMessage.get();
    }

}
