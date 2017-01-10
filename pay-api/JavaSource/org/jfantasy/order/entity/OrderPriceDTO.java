package org.jfantasy.order.entity;

import java.math.BigDecimal;

public class OrderPriceDTO {
    /**
     * 订单金额编码
     */
    private String code;
    /**
     * 金额
     */
    private BigDecimal value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
