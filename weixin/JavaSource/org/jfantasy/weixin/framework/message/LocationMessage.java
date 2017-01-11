package org.jfantasy.weixin.framework.message;

import org.jfantasy.weixin.framework.message.content.Location;

import java.util.Date;

/**
 * 地理位置消息
 */
public class LocationMessage extends AbstractWeixinMessage<Location> {

    public LocationMessage(Long id, String fromUserName, Date createTime) {
        super(id, fromUserName, createTime);
    }

}
