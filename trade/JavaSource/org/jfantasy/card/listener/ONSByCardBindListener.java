package org.jfantasy.card.listener;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import org.hibernate.Session;
import org.jfantasy.aliyun.AliyunSettings;
import org.jfantasy.autoconfigure.TradeAutoConfiguration;
import org.jfantasy.framework.jackson.FilterItem;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.lucene.dao.hibernate.OpenSessionUtils;
import org.jfantasy.card.bean.Card;
import org.jfantasy.card.bean.CardDesign;
import org.jfantasy.card.event.CardBindEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 发送消息 - 阿里云
 */
@Component
public class ONSByCardBindListener implements ApplicationListener<CardBindEvent> {

    @Resource(name = "pay.aliyunSettings")
    private AliyunSettings aliyunSettings;

    private final Producer producer;

    @Autowired(required = false)
    public ONSByCardBindListener(Producer producer) {
        this.producer = producer;
    }

    @Override
    @Async
    public void onApplicationEvent(CardBindEvent event) {
        Session session = OpenSessionUtils.openSession();
        try {
            Card card = event.getCard();
            Message msg = new Message(aliyunSettings.getTopicId(), TradeAutoConfiguration.ONS_TAGS_CARDBIND, card.getNo(), JSON.serialize(card, () -> new FilterItem[]{FilterItem.ignore(Card.class, "type", "batch", Card.FIELDS_BY_CREATOR, "create_time,", Card.FIELDS_BY_MODIFIER, "modify_time"), FilterItem.allow(CardDesign.class, "styles", "extras")}).getBytes());
            producer.send(msg);
        } finally {
            OpenSessionUtils.closeSession(session);
        }
    }

}
