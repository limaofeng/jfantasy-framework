package org.jfantasy.pay.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.pay.bean.converter.OrderItemConverter;
import org.jfantasy.pay.order.entity.OrderDetails;
import org.jfantasy.pay.order.entity.OrderItem;
import org.jfantasy.pay.order.entity.OrderKey;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@IdClass(OrderKey.class)
@Table(name = "PAY_ORDER")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "details"})
public class Order extends BaseBusEntity {

    // 付款状态（未支付、部分支付、已支付、部分退款、全额退款）
    public enum PaymentStatus {
        unpaid("未支付"), paid("已支付"), partRefund("部分退款"), refunded("全额退款");//NOSONAR

        private String value;

        PaymentStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 编号
     */
    @Id
    private String sn;
    /**
     * 订单类型
     */
    @Id
    private String type;
    /**
     * 支付状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", length = 20, nullable = false)
    private PaymentStatus status;
    /**
     * 订单摘要
     */
    @Column(name = "SUBJECT", length = 250)
    private String subject;
    /**
     * 订单详情
     */
    @Column(name = "BODY", length = 500)
    private String body;
    /**
     * 订单总金额
     */
    @Column(name = "TOTAL_FEE", nullable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal totalFee;
    /**
     * 订单应付金额
     */
    @Column(name = "PAYABLE_FEE", nullable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal payableFee;
    /**
     * 支付配置名称
     */
    @Column(name = "PAYMENT_CONFIG_NAME")
    private String payConfigName;
    /**
     * 付款时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PAYMENT_TIME")
    private Date paymentTime;
    /**
     * 退款时间
     */
    @Column(name = "REFUND_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date refundTime;
    /**
     * 退款金额
     */
    @Column(name = "REFUND_AMOUNT", precision = 15, scale = 2)
    private BigDecimal refundAmount;
    @Column(name = "MEMBER_ID", nullable = false, updatable = false)
    private Long memberId;
    @Transient
    private transient OrderDetails details;
    /**
     * 订单项
     */
    @Column(name = "ORDERITEM_STORE", length = 3000)
    @Convert(converter = OrderItemConverter.class)
    private List<OrderItem> orderItems;
    /**
     * 支付记录
     */
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<Payment> payments = new ArrayList<>();
    /**
     * 退款记录
     */
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<Refund> refunds = new ArrayList<>();

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getPayableFee() {
        return payableFee;
    }

    public void setPayableFee(BigDecimal payableFee) {
        this.payableFee = payableFee;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<Refund> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<Refund> refunds) {
        this.refunds = refunds;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    @Transient
    public String getKey() {
        return OrderKey.newInstance(this.type, this.sn).toString();
    }

    public void setDetails(OrderDetails details) {
        this.details = details;
    }

    public OrderDetails getDetails() {
        return details;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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

    public String getPayConfigName() {
        return payConfigName;
    }

    public void setPayConfigName(String payConfigName) {
        this.payConfigName = payConfigName;
    }
}
