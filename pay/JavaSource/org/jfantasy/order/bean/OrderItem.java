package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单明细表
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-22 下午1:59:27
 */
@Entity
@Table(name = "TRADE_ORDER_ITEM")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "memeo", "order"})
public class OrderItem extends BaseBusEntity {

    private static final long serialVersionUID = 5030818078599298690L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_item_gen")
    @TableGenerator(name = "order_item_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "mall_order_item:id", valueColumnName = "gen_value")
    @Column(name = "ID", updatable = false)
    private Long id;
    /**
     * 编号
     */
    @Column(name = "SN", nullable = false, updatable = false)
    private String sn;
    /**
     * 名称
     */
    @Column(name = "NAME", nullable = false, updatable = false)
    private String name;
    /**
     * 产品ID
     */
    @Column(name = "PRODUCT_ID", nullable = false, precision = 15, scale = 2)
    private String productId;
    /**
     * 产品类型
     */
    @Column(name = "PRODUCT_TYPE", nullable = false, precision = 15, scale = 2)
    private BigDecimal productType;
    /**
     * 产品编号
     */
    @Column(name = "PRODUCT_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal productPrice;
    /**
     * 产品数量
     */
    @Column(name = "PRODUCT_WEIGHT", nullable = false)
    private Integer productWeight;
    /**
     * 产品数量
     */
    @Column(name = "PRODUCT_QUANTITY", nullable = false)
    private Integer productQuantity;
    /**
     * 交付数量
     */
    @Column(name = "DELIVERY_QUANTITY", nullable = false)
    private Integer deliveryQuantity;
    /**
     * 订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_ITEM_ORDER"))
    private Order order;
    /**
     * 产品明细目录
     */
    @OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<OrderItemPrice> prices = new ArrayList<>();
    /**
     * 商品冗余属性
     */
    @Convert(converter = MapConverter.class)
    @Column(name = "attr", length = 4000)
    private HashMap<String, Object> attrs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getDeliveryQuantity() {
        return deliveryQuantity;
    }

    public void setDeliveryQuantity(Integer deliveryQuantity) {
        this.deliveryQuantity = deliveryQuantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<OrderItemPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<OrderItemPrice> prices) {
        this.prices = prices;
    }

    public void setAttrs(HashMap<String, Object> attrs) {
        this.attrs = attrs;
    }

    public BigDecimal getProductType() {
        return productType;
    }

    public void setProductType(BigDecimal productType) {
        this.productType = productType;
    }

    public Integer getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(Integer productWeight) {
        this.productWeight = productWeight;
    }

    /**
     * 重量小计
     *
     * @return Integer
     */
    @Transient
    public Integer getSubtotalWeight() {
        return this.getProductWeight() * this.getProductQuantity();
    }

    /**
     * 价格小计
     *
     * @return BigDecimal
     */
    @Transient
    public BigDecimal getSubtotalPrice() {
        return productPrice.multiply(new BigDecimal(productQuantity));
    }

    public void setAttr(HashMap<String, Object> attrs) {
        this.attrs = attrs;
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
    public Map<String, Object> getAttrs() {
        return attrs;
    }

}