package org.jfantasy.member.listener;

import org.jfantasy.member.service.WalletService;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.enums.AccountType;
import org.jfantasy.trade.event.AccountAmountChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UpdateWalletListener implements ApplicationListener<AccountAmountChangedEvent> {

    private WalletService walletService;

    @Async
    @Override
    public void onApplicationEvent(AccountAmountChangedEvent event) {
        Account account = event.getAccount();
        if (AccountType.personal == account.getType()) {
            this.walletService.saveOrUpdateWallet(event.getAccount());
        }
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

}
