package org.jfantasy.weixin.framework.handler;

import org.jfantasy.weixin.framework.message.*;
import org.jfantasy.weixin.framework.session.WeixinSession;

/**
 * 文本消息 hander
 */
public abstract class TextWeixinHandler extends AbstracWeixinHandler {

    @Override
    protected WeixinMessage handleImageMessage(WeixinSession session, ImageMessage message) {
        return EmptyMessage.get();
    }

    @Override
    protected WeixinMessage handleVideoMessage(WeixinSession session, VideoMessage message) {
        return EmptyMessage.get();
    }

    @Override
    protected WeixinMessage handleLocationMessage(WeixinSession session, LocationMessage message) {
        return EmptyMessage.get();
    }

    @Override
    protected WeixinMessage handleVoiceMessage(WeixinSession session, VoiceMessage message) {
        return EmptyMessage.get();
    }

    @Override
    protected WeixinMessage handleEventMessage(WeixinSession session, EventMessage message) {
        return EmptyMessage.get();
    }

    @Override
    protected WeixinMessage handleLinkMessage(WeixinSession session, LinkMessage message) {
        return EmptyMessage.get();
    }

}
