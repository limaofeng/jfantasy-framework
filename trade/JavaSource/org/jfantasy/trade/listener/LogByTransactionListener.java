package org.jfantasy.trade.listener;

import org.jfantasy.pay.bean.enums.OwnerType;
import org.jfantasy.pay.service.LogService;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.event.TransactionAddedEvent;
import org.jfantasy.trade.event.TransactionChangedEvent;
import org.jfantasy.trade.event.source.TxnSource;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 记录交易日志
 */
@Component
public class LogByTransactionListener implements SmartApplicationListener {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == TransactionChangedEvent.class || eventType == TransactionAddedEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == TxnSource.class;
    }

    @Async
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        Transaction transaction = ((TxnSource) event.getSource()).getTransaction();
        LogService.getInstance().log(OwnerType.transaction, transaction.getSn(), transaction.getStatus().name(), transaction.getNotes());
    }

}
