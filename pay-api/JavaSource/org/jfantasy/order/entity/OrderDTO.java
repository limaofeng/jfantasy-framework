package org.jfantasy.order.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.jfantasy.order.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 订单接口类
 */
public class OrderDTO {
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
     * 收货地址ID
     */
    private Long receiverId;
    /**
     * 配送类型ID
     */
    private Long deliveryTypeId;
    /**
     * 订单项
     */
    private List<OrderItemDTO> items = new ArrayList<>();
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
    /**
     * 备忘
     */
    private String memo;

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

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> orderItems) {
        this.items = orderItems;
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

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getDeliveryTypeId() {
        return deliveryTypeId;
    }

    public void setDeliveryTypeId(Long deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void addItem(OrderItemDTO item) {
        this.items.add(item);
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "sn='" + sn + '\'' +
                ", type='" + type + '\'' +
                ", totalFee=" + totalFee +
                ", payableFee=" + payableFee +
                ", memberId=" + memberId +
                '}';
    }

}
