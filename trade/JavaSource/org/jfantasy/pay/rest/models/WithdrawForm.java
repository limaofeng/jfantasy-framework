package org.jfantasy.pay.rest.models;


import org.hibernate.validator.constraints.NotEmpty;
import org.jfantasy.framework.spring.validation.RESTful;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

public class WithdrawForm {
    /**
     * 转出账户
     */
    private String from;
    /**
     * 金额
     */
    @NotNull(groups = RESTful.POST.class)
    private BigDecimal amount;
    /**
     * 支付密码
     */
    @NotEmpty(groups = RESTful.POST.class)
    private String password;
    /**
     * 冗余字段
     */
    private Map<String, Object> properties;
    /**
     * 备注
     */
    private String notes;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
