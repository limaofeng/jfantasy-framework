package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.jfantasy.common.Area;
import org.jfantasy.common.converter.AreaConverter;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.order.bean.enums.PaymentStatus;
import org.jfantasy.order.bean.enums.ShippingStatus;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.pay.bean.PayConfig;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;


/**
 * 订单表
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-21 下午4:21:42
 */
@Entity
@Table(name = "PAY_ORDER",uniqueConstraints = @UniqueConstraint(name = "UK_ORDER_DETAILS", columnNames = {"TARGET_TYPE", "TARGET_ID"}))
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "shipArea", "memeo", "shippings", "orderItems", "details_type", "details_id", "subject", "body"})
public class Order extends BaseBusEntity {

    private static final long serialVersionUID = -8541323033439515148L;

    @Id
    @Column(name = "SN", nullable = false, unique = true)
    @GenericGenerator(name = "serialnumber", strategy = "serialnumber", parameters = {@org.hibernate.annotations.Parameter(name = "expression", value = "#DateUtil.format('yyyyMMdd') + #StringUtil.addZeroLeft(#SequenceInfo.nextValue('ORDER-SN'), 5)")})
    private String id;// 订单编号
    @Column(name = "TYPE", length = 20, updatable = false, nullable = false)
    private String type;//订单类型
    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", length = 20, nullable = false)
    private OrderStatus status;// 订单状态
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", length = 20, nullable = false)
    private PaymentStatus paymentStatus;// 支付状态
    @Enumerated(EnumType.STRING)
    @Column(name = "SHIPPING_STATUS", length = 20, nullable = false)
    private ShippingStatus shippingStatus;// 发货状态
    @Column(name = "TOTAL_PRODUCT_WEIGHT", nullable = false)
    private Integer totalProductWeight;// 总商品重量(单位: 克)
    @Column(name = "TOTAL_PRODUCT_QUANTITY", nullable = false)
    private Integer totalProductQuantity;// 总商品数量
    @Column(name = "TOTAL_PRODUCT_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalProductPrice;// 总商品价格
    @Column(name = "SHIP_NAME", nullable = false)
    private String shipName;// 收货人姓名
    @Column(name = "SHIP_AREA_STORE", nullable = false)
    @Convert(converter = AreaConverter.class)
    private Area shipArea;// 收货地区存储
    @Column(name = "SHIP_ADDRESS", nullable = false)
    private String shipAddress;// 收货地址
    @Column(name = "SHIP_ZIP_CODE", nullable = false)
    private String shipZipCode;// 收货邮编
    @Column(name = "SHIP_MOBILE", nullable = false)
    private String shipMobile;// 收货手机
    @Column(name = "MEMO")
    private String memo;// 买家附言
    @Column(name = "DELIVERY_TYPE_NAME", length = 100)
    private String deliveryTypeName;// 配送方式名称
    @Column(name = "DELIVERY_TYPE_ID")
    private Long deliveryTypeId;// 配送方式
    @Column(name = "PAY_CONFIG_NAME", nullable = false)
    private String payConfigName;// 支付方式名称
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAY_CONFIG_ID", foreignKey = @ForeignKey(name = "FK_ORDER_PAY_CONFIG"))
    private PayConfig paymentConfig;// 支付方式
    @Column(name = "DELIVERY_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal deliveryAmount;// 配送费用
    @Column(name = "TOTAL_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;// 订单总额
    @Column(name = "PAID_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal paidAmount;// 已付金额
    @Column(name = "PAYABLE_AMOUNT", nullable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal payableAmount;//订单应付金额
    @Column(name = "PAYMENT_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentTime;//付款时间
    @Column(name = "REFUND_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date refundTime;//退款时间
    @Column(name = "REFUND_AMOUNT", precision = 15, scale = 2)
    private BigDecimal refundAmount;//退款金额
    @Column(name = "MEMBER_ID", nullable = false, updatable = false)
    private Long memberId;// 会员
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("createTime asc")
    private List<OrderItem> items = new ArrayList<>();// 订单支付信息
    @Convert(converter = MapConverter.class)
    @Column(name = "PROPERTIES", columnDefinition = "Text")
    private Map<String, Object> attrs;//NOSONAR 扩展属性
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<OrderPrice> prices = new ArrayList<>();//订单价格目录
    @Column(name = "TARGET_TYPE", nullable = false, updatable = false)
    private String detailsType;
    @JoinColumn(name = "TARGET_ID", nullable = false, updatable = false)
    private String detailsId;

    public OrderStatus getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public ShippingStatus getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(ShippingStatus shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public BigDecimal getTotalProductPrice() {
        return totalProductPrice;
    }

    public void setTotalProductPrice(BigDecimal totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }

    public BigDecimal getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(BigDecimal deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Integer getTotalProductWeight() {
        return totalProductWeight;
    }

    public void setTotalProductWeight(Integer totalProductWeight) {
        this.totalProductWeight = totalProductWeight;
    }

    public Integer getTotalProductQuantity() {
        return totalProductQuantity;
    }

    public void setTotalProductQuantity(Integer totalProductQuantity) {
        this.totalProductQuantity = totalProductQuantity;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OrderItem> getItems() {
        return ObjectUtil.defaultValue(items, Collections.emptyList());
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getDeliveryTypeName() {
        return deliveryTypeName;
    }

    public void setDeliveryTypeName(String deliveryTypeName) {
        this.deliveryTypeName = deliveryTypeName;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public Area getShipArea() {
        return shipArea;
    }

    public void setShipArea(Area shipArea) {
        this.shipArea = shipArea;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getShipZipCode() {
        return shipZipCode;
    }

    public void setShipZipCode(String shipZipCode) {
        this.shipZipCode = shipZipCode;
    }

    public String getShipMobile() {
        return shipMobile;
    }

    public void setShipMobile(String shipMobile) {
        this.shipMobile = shipMobile;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Long getDeliveryTypeId() {
        return deliveryTypeId;
    }

    public void setDeliveryTypeId(Long deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
    }

    public String getPayConfigName() {
        return payConfigName;
    }

    public void setPayConfigName(String payConfigName) {
        this.payConfigName = payConfigName;
    }

    public PayConfig getPaymentConfig() {
        return paymentConfig;
    }

    public void setPaymentConfig(PayConfig paymentConfig) {
        this.paymentConfig = paymentConfig;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getDetailsType() {
        return detailsType;
    }

    public void setDetailsType(String detailsType) {
        this.detailsType = detailsType;
    }

    public String getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(String detailsId) {
        this.detailsId = detailsId;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public List<OrderPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<OrderPrice> prices) {
        this.prices = prices;
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

    public Object get(String key) {
        if (this.attrs == null) {
            return null;
        }
        return this.attrs.get(key);
    }

    @JsonAnyGetter
    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    @Transient
    public boolean isExpired() {
        return SpringContextUtil.getBeanByType(OrderService.class).isExpired(this);
    }

    @Transient
    public String getSubject() {
        return "";
    }

    @Transient
    public String getBody() {
        return "";
    }

    @Transient
    public void addItems(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

}
