package org.jfantasy.pay.bean.enums;

/**
 * 交易状态
 */
public enum TxStatus {
    /**
     * 未处理
     */
    unprocessed("未处理"),
    /**
     * 处理中
     */
    processing("处理中"),
    /**
     * 成功
     */
    success("成功"),
    /**
     * 关闭
     */
    close("关闭");

    private final String value;

    TxStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
