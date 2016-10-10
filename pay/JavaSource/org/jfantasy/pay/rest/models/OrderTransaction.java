package org.jfantasy.pay.rest.models;

import org.jfantasy.pay.bean.Project;

import java.math.BigDecimal;

public class OrderTransaction {

    public enum Type {

        payment(Project.ORDER_PAYMENT), refund(Project.ORDER_REFUND);//NOSONAR

        private String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    private Type type;
    private BigDecimal amount;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
