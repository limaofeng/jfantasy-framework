package org.jfantasy.weixin.framework.handler;

import org.jfantasy.weixin.framework.message.EmptyMessage;
import org.jfantasy.weixin.framework.message.TextMessage;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动回复处理器
 */
public class AutoReplyTextHandler extends TextWeixinHandler {

    private final static Log LOG = LogFactory.getLog(AutoReplyTextHandler.class);

    private List<AutoReplyHandler> handlers = new ArrayList<AutoReplyHandler>();

    @Override
    protected WeixinMessage handleTextMessage(WeixinSession session, TextMessage message) {
        String keyword = message.getContent();
        for (AutoReplyHandler handler : handlers) {
            if (handler.handle(keyword)) {
                LOG.debug(keyword + " => " + handler);
                return handler.autoReply(keyword);
            }
        }
        return new EmptyMessage();
    }

    public void setHandlers(List<AutoReplyHandler> handlers) {
        this.handlers = handlers;
    }

}
