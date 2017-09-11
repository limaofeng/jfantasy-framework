package org.jfantasy.weixin.framework.factory;

import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.weixin.event.WeixinEventMessageEvent;
import org.jfantasy.weixin.event.WeixinMessageEvent;
import org.jfantasy.weixin.framework.account.WeixinAppService;
import org.jfantasy.weixin.framework.core.WeixinCoreHelper;
import org.jfantasy.weixin.framework.event.WeixinEventListener;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.handler.WeixinHandler;
import org.jfantasy.weixin.framework.intercept.DefaultInvocation;
import org.jfantasy.weixin.framework.intercept.WeixinEventMessageInterceptor;
import org.jfantasy.weixin.framework.intercept.WeixinMessageInterceptor;
import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.DefaultWeixinSession;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultWeixinSessionFactory implements WeixinSessionFactory {

    private WeixinCoreHelper weixinCoreHelper;

    private WeixinAppService weixinAppService;

    private ApplicationContext applicationContext;

    private Class<? extends WeixinSession> sessionClass = DefaultWeixinSession.class;

    private WeixinHandler messageHandler;

    private WeixinHandler eventHandler;

    private List<WeixinMessageInterceptor> weixinMessageInterceptors = new ArrayList<>();

    private Map<EventMessage.EventType, List<WeixinEventListener>> eventListeners = new EnumMap(EventMessage.EventType.class);

    private ConcurrentMap<String, WeixinSession> weiXinSessions = new ConcurrentHashMap<>();

    public DefaultWeixinSessionFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public WeixinCoreHelper getWeixinCoreHelper() {
        return this.weixinCoreHelper;
    }

    @Override
    public WeixinSession getCurrentSession() throws WeixinException {
        return WeixinSessionUtils.getCurrentSession();
    }

    @Override
    public WeixinSession openSession(String appid) throws WeixinException {
        if (!weiXinSessions.containsKey(appid)) {
            weiXinSessions.putIfAbsent(appid, new DefaultWeixinSession(this.weixinAppService.loadAccountByAppid(appid), weixinCoreHelper));
        }
        return weiXinSessions.get(appid);
    }

    public WeixinAppService getWeixinAppService() {
        return this.weixinAppService;
    }

    @Override
    public WeixinMessage<?> execute(WeixinMessage message) throws WeixinException {
        List<Object> handler = new ArrayList<>(weixinMessageInterceptors);
        if (message instanceof EventMessage) {
            applicationContext.publishEvent(new WeixinEventMessageEvent((EventMessage) message));//事件推送
            final List<WeixinEventListener> listeners = ObjectUtil.defaultValue(eventListeners.get(((EventMessage) message).getEventType()), Collections.<WeixinEventListener>emptyList());
            handler.add(new WeixinEventMessageInterceptor(((EventMessage) message).getEventType(), listeners));//添加Event拦截器,用于触发事件
            handler.add(this.eventHandler);
        } else {
            applicationContext.publishEvent(new WeixinMessageEvent(message));//消息事件推送
            handler.add(this.messageHandler);
        }
        return new DefaultInvocation(WeixinSessionUtils.getCurrentSession(), message, handler.iterator()).invoke();
    }

    public Class<? extends WeixinSession> getSessionClass() {
        return sessionClass;
    }

    public void setSessionClass(Class<? extends WeixinSession> sessionClass) {
        this.sessionClass = sessionClass;
    }

    public void setWeixinAppService(WeixinAppService weixinAppService) {
        this.weixinAppService = weixinAppService;
    }

    public void setWeixinCoreHelper(WeixinCoreHelper weixinCoreHelper) {
        this.weixinCoreHelper = weixinCoreHelper;
    }

    public void setEventHandler(WeixinHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void setMessageHandler(WeixinHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setWeixinMessageInterceptors(List<WeixinMessageInterceptor> weixinMessageInterceptors) {
        this.weixinMessageInterceptors = weixinMessageInterceptors;
    }

    public void setEventListeners(Map<EventMessage.EventType, List<WeixinEventListener>> eventListeners) {
        this.eventListeners.putAll(eventListeners);
    }

}
