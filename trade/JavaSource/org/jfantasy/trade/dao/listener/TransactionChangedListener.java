package org.jfantasy.trade.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.event.TransactionChangedEvent;
import org.springframework.stereotype.Component;

@Component
public class TransactionChangedListener extends AbstractChangedListener<Transaction> {

    private static final long serialVersionUID = 6486933157808350841L;

    public TransactionChangedListener(){
        super(EventType.POST_COMMIT_UPDATE);
    }

    @Override
    public void onPostUpdate(Transaction transaction, PostUpdateEvent event) {
        if (modify(event, "status")) {
            applicationContext.publishEvent(new TransactionChangedEvent(transaction.getStatus(), transaction));
        }
    }

}
