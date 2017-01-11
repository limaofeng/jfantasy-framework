package org.jfantasy.weixin.listener;

import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.weixin.framework.event.SubscribeEventListener;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.content.Event;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.jfantasy.weixin.service.FansService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executor;

public class SubscribeListener implements SubscribeEventListener {

    private final static Log LOG = LogFactory.getLog(SubscribeListener.class);

    @Autowired
    private FansService fansService;

    @Override
    public void onSubscribe(final WeixinSession session, Event event, final EventMessage message) {
        Executor executor = SpringContextUtil.getBeanByType(Executor.class);
        assert executor != null;
        executor.execute(() -> {
            try {
                WeixinSessionUtils.saveSession(session);
                fansService.checkCreateMember(session.getWeixinApp().getId(),message.getFromUserName());
            } catch (WeixinException e) {
                LOG.error(e.getMessage(), e);
            } finally {
                WeixinSessionUtils.closeSession();
            }
        });
    }
}
