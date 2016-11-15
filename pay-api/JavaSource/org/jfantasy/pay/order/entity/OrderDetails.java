package org.jfantasy.pay.order.entity;

import org.jfantasy.pay.order.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单接口类
 */
public class OrderDetails {
    /**
     * 编号
     */
    private String sn;
    /**
     * 订单类型
     */
    private String type;
    /**
     * 订单状态
     */
    private OrderStatus status;
    /**
     * 订单摘要
     */
    private String subject;
    /**
     * 订单详情
     */
    private String body;
    /**
     * 订单总金额
     */
    private BigDecimal totalFee;
    /**
     * 订单应付金额
     */
    private BigDecimal payableFee;
    /**
     * 订单项
     */
    private List<OrderItem> orderItems;
    /**
     * 订单关联的会员
     */
    private Long memberId;
    /**
     * 订单下单时间
     */
    private Date orderTime;
    /**
     * 付款时间
     */
    private Date paymentTime;
    /**
     * 退款时间
     */
    private Date refundTime;
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    /**
     * 扩展属性
     */
    private Map<String, Object> properties;

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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
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

    @Override
    public String toString() {
        return "OrderDetails{" +
                "sn='" + sn + '\'' +
                ", type='" + type + '\'' +
                ", subject='" + subject + '\'' +
                ", Body='" + body + '\'' +
                ", totalFee=" + totalFee +
                ", payableFee=" + payableFee +
                ", orderItems=" + orderItems +
                ", memberId=" + memberId +
                ", properties=" + properties +
                '}';
    }
}
