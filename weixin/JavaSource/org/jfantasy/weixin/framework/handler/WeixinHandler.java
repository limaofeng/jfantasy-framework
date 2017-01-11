package org.jfantasy.weixin.framework.handler;

import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.WeixinSession;

public interface WeixinHandler {

    /**
     * 消息处理类
     *
     * @param session 微信回话
     * @param message 微信消息
     * @throws WeixinException
     */
    WeixinMessage<?> handleMessage(WeixinSession session, WeixinMessage<?> message) throws WeixinException;

}
