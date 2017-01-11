package org.jfantasy.weixin.framework.handler;

import org.jfantasy.weixin.framework.message.EmptyMessage;
import org.jfantasy.weixin.framework.message.TextMessage;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.WeixinSession;

public class NotReplyTextHandler extends TextWeixinHandler {

    @Override
    protected WeixinMessage handleTextMessage(WeixinSession session, TextMessage message) {
        return EmptyMessage.get();
    }

}
