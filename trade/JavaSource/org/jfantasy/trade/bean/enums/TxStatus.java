package org.jfantasy.trade.bean.enums;

/**
 * 交易状态
 */
public enum TxStatus {
    /**
     * 未处理
     */
    unprocessed("未处理"),//NOSONAR
    /**
     * 处理中
     */
    processing("处理中"),//NOSONAR
    /**
     * 成功
     */
    success("成功"),//NOSONAR
    /**
     * 关闭
     */
    close("关闭");//NOSONAR

    private final String value;

    TxStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
