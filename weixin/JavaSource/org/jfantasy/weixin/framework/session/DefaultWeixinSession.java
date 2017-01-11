package org.jfantasy.weixin.framework.session;

import org.jfantasy.weixin.framework.core.WeixinCoreHelper;

/**
 * 微信Session默认实现
 */
public class DefaultWeixinSession extends AbstractWeixinSession {

    public DefaultWeixinSession(WeixinApp weixinApp, WeixinCoreHelper weixinCoreHelper) {
        super(weixinApp, weixinCoreHelper);
    }

}
