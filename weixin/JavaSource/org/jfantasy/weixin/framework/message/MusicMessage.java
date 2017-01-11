package org.jfantasy.weixin.framework.message;

import org.jfantasy.weixin.framework.message.content.Music;

/**
 * 音乐消息
 */
public class MusicMessage extends AbstractWeixinMessage<Music> {

    public MusicMessage(Music content) {
        super(content);
    }

}
