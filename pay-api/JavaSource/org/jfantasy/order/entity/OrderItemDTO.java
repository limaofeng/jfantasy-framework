package org.jfantasy.order.entity;

import java.math.BigDecimal;

/**
 * 订单明细项
 */
public class OrderItemDTO {
    /**
     * 编号
     */
    private String sn;
    /**
     * 名称
     */
    private String name;
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 产品金额（单价）
     */
    private BigDecimal productPrice;
    /**
     * 产品数量
     */
    private Integer productQuantity;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

}
