package org.jfantasy.trade.listener;

import org.jfantasy.trade.event.TransactionChangedEvent;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AutoProcessTransactionListener implements ApplicationListener<TransactionChangedEvent> {

    private TransactionService transactionService;

    @Async
    @Override
    public void onApplicationEvent(TransactionChangedEvent event) {
        transactionService.process(event.getTransaction().getSn());
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

}
