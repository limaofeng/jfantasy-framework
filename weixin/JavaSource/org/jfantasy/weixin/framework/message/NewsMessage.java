package org.jfantasy.weixin.framework.message;

import org.jfantasy.weixin.framework.message.content.News;

import java.util.List;

/**
 * 图文消息
 */
public class NewsMessage extends AbstractWeixinMessage<List<News>> {

    public NewsMessage(List<News> content) {
        super(content);
    }

}
