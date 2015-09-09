package com.fantasy.payment.bean;

import com.fantasy.framework.dao.BaseBusEntity;
import com.fantasy.member.bean.Member;
import com.fantasy.member.bean.databind.MemberDeserializer;
import com.fantasy.member.bean.databind.MemberSerializer;
import com.fantasy.payment.bean.databind.PaymentConfigDeserializer;
import com.fantasy.payment.bean.databind.PaymentConfigSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 支付
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-12-5 上午9:22:59
 */
@ApiModel(value = "支付记录")
@Entity
@Table(name = "MALL_PAYMENT")
public class Payment extends BaseBusEntity {

    private static final long serialVersionUID = 6404772131152718534L;

    // 支付类型（在线支付、线下支付）
    public enum PaymentType {
        online, offline
    }

    // 支付状态（准备、超时、作废、成功、失败）
    public enum PaymentStatus {
        ready, timeout, invalid, success, failure
    }

    @Id
    @Column(name = "ID", insertable = true, updatable = false)
    @GeneratedValue(generator = "fantasy-sequence")
    @GenericGenerator(name = "fantasy-sequence", strategy = "fantasy-sequence")
    private Long id;
    /**
     * 支付编号
     */
    @ApiModelProperty("支付编号")
    @Column(name = "SN", nullable = false, updatable = false, unique = true)
    @GenericGenerator(name = "serialnumber", strategy = "serialnumber", parameters = {@Parameter(name = "expression", value = "'SN_' + #DateUtil.format('yyyyMMdd') + #StringUtil.addZeroLeft(#SequenceInfo.nextValue('PATMENT-SN'), 5)")})
    private String sn;
    /**
     * 交易号（用于记录第三方交易的交易流水号）
     */
    @ApiModelProperty(value = "交易号", notes = "用于记录第三方交易的交易流水号")
    @Column(name = "TRADE_NO", updatable = true)
    private String tradeNo;
    /**
     * 支付类型
     */
    @ApiModelProperty("支付类型")
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_TYPE", nullable = false, updatable = false)
    private PaymentType paymentType;
    /**
     * 支付配置名称
     */
    @ApiModelProperty("支付配置名称")
    @Column(name = "PAYMENT_CONFIG_NAME", nullable = false, updatable = false)
    private String paymentConfigName;
    /**
     * 收款银行名称
     */
    @ApiModelProperty("收款方名称")
    @Column(name = "BANK_NAME", updatable = false)
    private String bankName;
    /**
     * 收款银行账号
     */
    @ApiModelProperty("收款账号")
    @Column(name = "BANK_ACCOUNT", updatable = false)
    private String bankAccount;
    /**
     * 支付金额
     */
    @ApiModelProperty("支付金额")
    @Column(name = "TOTAL_AMOUNT", nullable = false, updatable = false, precision = 15, scale = 5)
    private BigDecimal totalAmount;
    /**
     * 支付手续费
     */
    @ApiModelProperty("支付手续费")
    @Column(name = "PAYMENT_FEE", nullable = false, updatable = false, precision = 15, scale = 5)
    private BigDecimal paymentFee;
    /**
     * 付款人
     */
    @ApiModelProperty("付款人")
    @Column(name = "PAYER", updatable = false)
    private String payer;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @Column(name = "MEMO", updatable = false, length = 3000)
    private String memo;
    /**
     * 支付状态
     */
    @ApiModelProperty("支付状态")
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", nullable = false)
    private PaymentStatus paymentStatus;
    /**
     * 会员
     */
    @ApiModelProperty(hidden = true)
    @JsonProperty("memberId")
    @JsonSerialize(using = MemberSerializer.class)
    @JsonDeserialize(using = MemberDeserializer.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", foreignKey = @ForeignKey(name = "FK_PAYMENT_MEMBER"))
    private Member member;
    /**
     * 支付方式
     */
    @ApiModelProperty(hidden = true)
    @JsonProperty("paymentConfigId")
    @JsonSerialize(using = PaymentConfigSerializer.class)
    @JsonDeserialize(using = PaymentConfigDeserializer.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_CONFIG_ID", foreignKey = @ForeignKey(name = "FK_PAYMENT_PAYMENT_CONFIG"))
    private PaymentConfig paymentConfig;
    /**
     * 订单类型
     */
    @ApiModelProperty("订单类型")
    @Column(name = "ORDER_TYPE")
    private String orderType;
    /**
     * 订单编号
     */
    @ApiModelProperty("订单编号")
    @Column(name = "ORDER_SN")
    private String orderSn;

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

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentConfigName() {
        return paymentConfigName;
    }

    public void setPaymentConfigName(String paymentConfigName) {
        this.paymentConfigName = paymentConfigName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaymentFee() {
        return paymentFee;
    }

    public void setPaymentFee(BigDecimal paymentFee) {
        this.paymentFee = paymentFee;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public PaymentConfig getPaymentConfig() {
        return paymentConfig;
    }

    public void setPaymentConfig(PaymentConfig paymentConfig) {
        this.paymentConfig = paymentConfig;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }
}