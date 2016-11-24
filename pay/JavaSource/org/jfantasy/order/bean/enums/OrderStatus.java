package org.jfantasy.order.bean.enums;

// 订单状态（未处理、已处理、已完成、已作废）
public enum OrderStatus {
    /**
     * 未处理
     */
    UNPROCESSED("未处理"),
    /**
     * 已处理
     */
    PROCESSED("已处理"),
    /**
     * 已完成
     */
    COMPLETED("已完成"),
    /**
     * 已作废
     */
    INVALID("已作废");

    private String value;

    private OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
