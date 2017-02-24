package org.jfantasy.card.listener;

import org.hibernate.Session;
import org.jfantasy.card.bean.Card;
import org.jfantasy.card.bean.CardDesign;
import org.jfantasy.card.event.CardBindEvent;
import org.jfantasy.framework.jackson.FilterItem;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.lucene.dao.hibernate.OpenSessionUtils;
import org.jfantasy.ms.EventEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 发送消息 - 阿里云
 */
@Component
public class ONSByCardBindListener implements ApplicationListener<CardBindEvent> {

    private EventEmitter eventEmitter;

    @Override
    @Async
    public void onApplicationEvent(CardBindEvent event) {
        Session session = OpenSessionUtils.openSession();
        try {
            Card card = event.getCard();
            eventEmitter.fireEvent("card.inpour", card.getNo(), String.format("卡号[%s]充值成功", card.getNo()), JSON.serialize(card, () -> new FilterItem[]{FilterItem.ignore(Card.class, "type", "batch", Card.FIELDS_BY_CREATOR, "create_time,", Card.FIELDS_BY_MODIFIER, "modify_time"), FilterItem.allow(CardDesign.class, "styles", "extras")}));
        } finally {
            OpenSessionUtils.closeSession(session);
        }
    }

    @Autowired
    public void setEventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }


}
