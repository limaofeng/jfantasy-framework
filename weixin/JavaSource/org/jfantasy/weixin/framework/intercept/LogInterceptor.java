package org.jfantasy.weixin.framework.intercept;

import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 记录接收及发生的消息
 */
public class LogInterceptor implements WeixinMessageInterceptor {

    private final static Log LOG = LogFactory.getLog(LogInterceptor.class);

    @Override
    public WeixinMessage intercept(WeixinSession session, WeixinMessage message, Invocation invocation) throws WeixinException {

        LOG.debug("接收到的消息内容:" + message.getContent());

        WeixinMessage outMessage = invocation.invoke();

        LOG.debug("回复的消息内容:" + (outMessage == null ? "null" : outMessage.getContent()));

        return outMessage;
    }

}
