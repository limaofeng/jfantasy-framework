package org.jfantasy.pay.rest.models;

import org.jfantasy.trade.bean.enums.TxStatus;

import javax.validation.constraints.NotNull;

public class TxStatusForm {
    /**
     * 交易状态
     */
    @NotNull
    private TxStatus status;
    /**
     * 状态文本
     */
    private String statusText;
    /**
     * 备注
     */
    private String notes;

    public TxStatus getStatus() {
        return status;
    }

    public void setStatus(TxStatus status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
