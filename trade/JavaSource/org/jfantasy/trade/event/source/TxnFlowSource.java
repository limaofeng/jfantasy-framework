package org.jfantasy.trade.event.source;

public class TxnFlowSource {
    private Integer flow;
    private String txId;

    public TxnFlowSource(String txId,Integer flow) {
        this.flow = flow;
        this.txId = txId;
    }

    public Integer getFlow() {
        return flow;
    }

    public String getTxId() {
        return txId;
    }
}
