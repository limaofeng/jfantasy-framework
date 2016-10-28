package org.jfantasy.pay.bean;

import org.jfantasy.pay.bean.enums.BillType;

import java.io.Serializable;
import java.math.BigDecimal;

public class ReportItem implements Serializable {

    /**
     * 收支类型
     */
    private BillType type;
    /**
     * 编码
     */
    private String code;
    /**
     * 数值
     */
    private BigDecimal value;

    public ReportItem(){
    }

    public ReportItem(BillType type, String code, BigDecimal value) {
        this.type = type;
        this.code = code;
        this.value = value;
    }

    public BillType getType() {
        return type;
    }

    public void setType(BillType type) {
        this.type = type;
    }

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

    public void addValue(BigDecimal value) {
        this.value = this.value.add(value);
    }

}
