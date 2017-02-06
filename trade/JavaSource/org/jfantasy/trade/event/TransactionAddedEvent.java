package org.jfantasy.trade.event;


import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.event.source.TxnSource;
import org.springframework.context.ApplicationEvent;

public class TransactionAddedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1369840597253827146L;

    public TransactionAddedEvent(Transaction transaction) {
        super(new TxnSource(transaction.getStatus(),transaction));
    }

    public TxStatus getStatus() {
        return ((TxnSource)this.getSource()).getStatus();
    }

    public Transaction getTransaction() {
        return ((TxnSource)this.getSource()).getTransaction();
    }

}
