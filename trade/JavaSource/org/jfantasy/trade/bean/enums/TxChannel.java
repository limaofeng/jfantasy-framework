package org.jfantasy.trade.bean.enums;

/**
 * 交易渠道
 */
public enum TxChannel {

    /**
     * 线下
     */
    offline("线下"),
    /**
     * 会员卡
     */
    card("会员卡"),
    /**
     * 余额支付
     */
    internal("内部"),
    /**
     * 第三方支付平台
     */
    online("在线");

    private String value;

    TxChannel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
