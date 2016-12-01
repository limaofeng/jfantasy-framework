package org.jfantasy.card.listener;

import org.jfantasy.card.bean.Card;
import org.jfantasy.card.bean.enums.Usage;
import org.jfantasy.card.event.CardBindEvent;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 绑定卡的关联的实际逻辑
 */
@Component
public class CardBindListener implements ApplicationListener<CardBindEvent> {

    private final TransactionService transactionService;

    @Autowired
    public CardBindListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void onApplicationEvent(CardBindEvent event) {
        Card card = event.getCard();
        if(card.getUsage() != Usage.inpour){
            return;
        }
        transactionService.inpour(card);
    }

}
