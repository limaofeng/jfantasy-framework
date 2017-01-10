package org.jfantasy.order.entity;


public class OrderPayeeDTO {
    /**
     * 收款人编码
     */
    private String code;
    /**
     * 收款人名称
     */
    private String name;
    /**
     * 收款人
     */
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
