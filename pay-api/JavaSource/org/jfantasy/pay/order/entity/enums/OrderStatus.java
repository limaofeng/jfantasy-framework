package org.jfantasy.pay.order.entity.enums;

/**
 * 付款状态（未支付、部分支付、已支付、部分退款、全额退款）
 */
public enum  OrderStatus {
    unpaid("未支付"), paid("已支付"), partRefund("部分退款"), refunded("全额退款"), close("关闭");//NOSONAR

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
