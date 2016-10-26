package org.jfantasy.pay.rest.models;

import org.hibernate.validator.constraints.NotEmpty;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.pay.bean.enums.TxChannel;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionForm {
    /**
     * 转入账户
     */
    private String to;
    /**
     * 金额
     */
    @NotNull(groups = RESTful.POST.class)
    private BigDecimal amount;
    /**
     * 支付项目
     */
    @NotEmpty(groups = RESTful.POST.class)
    private String project;
    /**
     * 渠道
     */
    @NotNull(groups = RESTful.POST.class)
    private TxChannel channel;
    /**
     * 支付密码
     */
    @NotEmpty(groups = RESTful.POST.class)
    private String password;
    /**
     * 备注
     */
    private String notes;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TxChannel getChannel() {
        return channel;
    }

    public void setChannel(TxChannel channel) {
        this.channel = channel;
    }

}
