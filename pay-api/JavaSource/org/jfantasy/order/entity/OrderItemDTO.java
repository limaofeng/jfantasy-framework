package org.jfantasy.order.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.math.BigDecimal;
import java.util.HashMap;

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
     * 产品重量(单位克)
     */
    private Integer productWeight;
    /**
     * 产品数量
     */
    private Integer productQuantity;
    /**
     * 产品描述
     */
    private String productDescription;
    /**
     * 产品冗余属性
     */
    private HashMap<String, Object> attrs;

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

    public Integer getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(Integer productWeight) {
        this.productWeight = productWeight;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        if (value == null) {
            return;
        }
        if (this.attrs == null) {
            this.attrs = new HashMap<>();
        }
        this.attrs.put(key, value);
    }

    @JsonAnyGetter
    public HashMap<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(HashMap<String, Object> attrs) {
        this.attrs = attrs;
    }
}
