package org.jfantasy.member.listener;

import org.jfantasy.member.service.WalletService;
import org.jfantasy.trade.event.AccountChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UpdateWalletListener implements ApplicationListener<AccountChangedEvent> {

    private WalletService walletService;

    @Async
    @Override
    public void onApplicationEvent(AccountChangedEvent event) {
        this.walletService.saveOrUpdateWallet(event.getAccount());
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

}
