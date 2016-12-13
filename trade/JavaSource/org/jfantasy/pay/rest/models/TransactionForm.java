package org.jfantasy.pay.rest.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.hibernate.validator.constraints.NotEmpty;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.trade.bean.enums.TxChannel;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TransactionForm {
    /**
     * 转出账户
     */
    private String from;
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
     * 冗余字段
     */
    private Map<String, Object> properties;//NOSONAR
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

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void set(String key, String value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
    }

    public String get(String key) {
        if (this.properties == null || !this.properties.containsKey(key)) {
            return null;
        }
        return (String) this.properties.get(key);
    }

}
