package org.jfantasy.trade.event;


import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.event.source.TxnSource;
import org.springframework.context.ApplicationEvent;

public class TransactionChangedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1369840597253827146L;

    public TransactionChangedEvent(TxStatus status, Transaction transaction) {
        super(new TxnSource(status,transaction));
    }

    public TxStatus getStatus() {
        return ((TxnSource)this.getSource()).getStatus();
    }

    public Transaction getTransaction() {
        return ((TxnSource)this.getSource()).getTransaction();
    }

}
