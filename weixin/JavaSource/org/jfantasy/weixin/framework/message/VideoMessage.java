package org.jfantasy.weixin.framework.message;

import org.jfantasy.weixin.framework.message.content.Video;

import java.util.Date;

/**
 * 微信视频消息
 */
public class VideoMessage extends AbstractWeixinMessage<Video> {

    public VideoMessage(Long id, String fromUserName, Date createTime) {
        super(id, fromUserName, createTime);
    }

    public VideoMessage(Video content) {
        super(content);
    }

}
