package org.jfantasy.pay.listener;

import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.ProjectType;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.event.TransactionChangedEvent;
import org.jfantasy.pay.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransferListener implements ApplicationListener<TransactionChangedEvent> {

    private AccountService accountService;

    @Async
    @Override
    @Transactional
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        if (transaction.getStatus() != TxStatus.unprocessed) {
            return;
        }
        if (transaction.getProject().getType() != ProjectType.transfer) {
            return;
        }
        accountService.transfer(transaction.getSn(), "自动转账");
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

}
