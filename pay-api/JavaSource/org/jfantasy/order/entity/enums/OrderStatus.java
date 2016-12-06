package org.jfantasy.order.entity.enums;

/**
 * 付款状态（未支付、部分支付、已支付、部分退款、全额退款）
 */
public enum OrderStatus {
    unpaid("未支付"), paid("已支付"), refunding("退款中"), refunded("已退款"), closed("已关闭"), complete("已完成");//NOSONAR

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
