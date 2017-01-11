package org.jfantasy.weixin.framework.factory;

import org.jfantasy.weixin.framework.exception.NoSessionException;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.session.WeixinSession;

public class WeixinSessionUtils {

    /**
     * 当前 session 对象
     */
    private static ThreadLocal<WeixinSession> current = new ThreadLocal<>();

    public static WeixinSession getCurrentSession() throws WeixinException {
        if (current.get() == null) {
            throw new NoSessionException("未初始化 WeiXinSession 对象");
        }
        return current.get();
    }

    public static WeixinSession saveSession(WeixinSession session) {
        current.set(session);
        return session;
    }

    public static void closeSession() {
        current.remove();
    }
}
