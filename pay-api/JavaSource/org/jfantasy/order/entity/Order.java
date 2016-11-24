package org.jfantasy.order.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.jfantasy.order.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 订单接口类
 */
public class Order {
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
    private HashMap<String, Object> attrs;

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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

    @JsonAnyGetter
    public HashMap<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(HashMap<String, Object> attrs) {
        this.attrs = attrs;
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

    @Override
    public String toString() {
        return "OrderDetails{" +
                "sn='" + sn + '\'' +
                ", type='" + type + '\'' +
                ", totalFee=" + totalFee +
                ", payableFee=" + payableFee +
                ", orderItems=" + orderItems +
                ", memberId=" + memberId +
                '}';
    }

}
