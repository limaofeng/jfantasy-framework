package org.jfantasy.weixin.framework.factory;

import org.jfantasy.weixin.framework.core.WeixinCoreHelper;
import org.jfantasy.weixin.framework.account.WeixinAppService;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.WeixinSession;

public interface WeixinSessionFactory {

    /**
     * 第三方工具类
     *
     * @return Signature
     */
    WeixinCoreHelper getWeixinCoreHelper();

    /**
     * 获取当前的 WeiXinSession
     *
     * @return WeiXinSession
     * @throws WeixinException
     */
    WeixinSession getCurrentSession() throws WeixinException;

    /**
     * 返回一个 WeiXinSession 对象，如果当前不存在，则创建一个新的session对象
     *
     * @return WeiXinSession
     * @throws WeixinException
     */
    WeixinSession openSession(String appid) throws WeixinException;

    /**
     * 获取微信账号存储服务
     *
     * @return AccountDetailsService
     */
    WeixinAppService getWeixinAppService();

    /**
     * 处理接收到的请求
     *
     * @param message http response
     * @return WeiXinMessage
     */
    WeixinMessage<?> execute(WeixinMessage message) throws WeixinException;

}
