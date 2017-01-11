package org.jfantasy.weixin.framework.message;

import java.util.Date;

/**
 * 微信消息接口
 */
public interface WeixinMessage<T> {

    /**
     * MsgId	消息id，64位整型
     *
     * @return id
     */
    Long getId();

    /**
     * 发送方帐号（一个OpenID/微信原始ID）
     *
     * @return String
     */
    String getFromUserName();

    /**
     * 消息创建时间 （整型）
     *
     * @return date
     */
    Date getCreateTime();

    /**
     * 获取微信内容
     *
     * @return T
     */
    T getContent();

    /**
     * 接收方帐号（一个OpenID/微信原始ID）
     *
     * @return String
     */
    String getToUserName();

}
