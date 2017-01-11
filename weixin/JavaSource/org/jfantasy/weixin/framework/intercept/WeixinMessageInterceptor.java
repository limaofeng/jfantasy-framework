package org.jfantasy.weixin.framework.intercept;

import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.WeixinSession;

/**
 * 微信消息拦截器
 */
public interface WeixinMessageInterceptor {

    /**
     * 消息拦截器
     *
     * @param session    微信公众号
     * @param message    消息
     * @param invocation 调用
     * @return WeiXinMessage
     */
    WeixinMessage intercept(WeixinSession session, WeixinMessage message, Invocation invocation) throws WeixinException;

}
