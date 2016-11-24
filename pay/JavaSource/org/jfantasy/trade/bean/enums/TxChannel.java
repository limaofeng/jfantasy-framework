package org.jfantasy.trade.bean.enums;

/**
 * 交易渠道
 */
public enum TxChannel {
    /**
     * 线下交易(可用于余额提现)
     */
    offline,//NOSONAR
    /**
     * 内部交易（余额支付）
     */
    internal,//NOSONAR
    /**
     * 第三方交易平台
     */
    thirdparty//NOSONAR

}
