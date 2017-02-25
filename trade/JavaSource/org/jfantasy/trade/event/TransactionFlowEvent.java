package org.jfantasy.trade.event;

import org.jfantasy.trade.event.source.TxnFlowSource;
import org.springframework.context.ApplicationEvent;

public class TransactionFlowEvent extends ApplicationEvent {

    public TransactionFlowEvent(TxnFlowSource source) {
        super(source);
    }

    public String getTxId() {
        return ((TxnFlowSource) this.getSource()).getTxId();
    }

    public Integer getFlow() {
        return ((TxnFlowSource) this.getSource()).getFlow();
    }

}
