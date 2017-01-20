package org.jfantasy.member.listener;

import org.jfantasy.card.event.CardBindEvent;
import org.jfantasy.member.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CardBindToWalletListener  implements ApplicationListener<CardBindEvent> {

    private WalletService walletService;

    @Async
    @Override
    public void onApplicationEvent(CardBindEvent event) {
        this.walletService.addCard(event.getCard());
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

}
