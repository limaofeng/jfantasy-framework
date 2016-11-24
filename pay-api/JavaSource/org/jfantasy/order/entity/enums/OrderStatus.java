package org.jfantasy.order.entity.enums;

/**
 * 付款状态（未支付、部分支付、已支付、部分退款、全额退款）
 */
public enum  OrderStatus {
    UNPAID("未支付"), PAID("已支付"), PARTREFUND("部分退款"), REFUNDED("全额退款"), CLOSE("关闭");//NOSONAR

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
