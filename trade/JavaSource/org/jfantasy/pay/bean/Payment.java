package org.jfantasy.pay.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.bean.enums.PaymentType;
import org.jfantasy.trade.bean.Transaction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付记录
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-12-5 上午9:22:59
 */
@Entity
@Table(name = "PAY_PAYMENT", uniqueConstraints = {@UniqueConstraint(columnNames = {"PAY_CONFIG_ID", "ORDER_ID", "PAY_STATUS"}, name = "UK_PAYMENT_CONFIGID_ORDER_STATUS")})
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "transaction"})
public class Payment extends BaseBusEntity {

    private static final long serialVersionUID = 6404772131152718534L;

    /**
     * 支付编号
     */
    @Id
    @Column(name = "SN", updatable = false)
    @GeneratedValue(generator = "serialnumber")
    @GenericGenerator(name = "serialnumber", strategy = "serialnumber", parameters = {@Parameter(name = "expression", value = "'P' + (#systemProperties['spring.profiles.active'] == 'prod' ? '' : 'DEV') + #DateUtil.format('yyyyMMdd') + #StringUtil.addZeroLeft(#SequenceInfo.nextValue('PATMENT-SN' + #DateUtil.format('yyyyMMdd')), 5)")})
    private String sn;
    /**
     * 交易号（用于记录第三方交易的交易流水号）
     */
    @Column(name = "TRADE_NO")
    private String tradeNo;
    /**
     * 支付类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, updatable = false)
    private PaymentType type;
    /**
     * 支付配置名称
     */
    @Column(name = "PAYMENT_CONFIG_NAME", nullable = false, updatable = false)
    private String payConfigName;
    /**
     * 收款银行名称
     */
    @Column(name = "BANK_NAME", updatable = false)
    private String bankName;
    /**
     * 收款银行账号
     */
    @Column(name = "BANK_ACCOUNT", updatable = false)
    private String bankAccount;
    /**
     * 支付金额
     */
    @Column(name = "TOTAL_AMOUNT", nullable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;
    /**
     * 支付手续费
     */
    @Column(name = "PAYMENT_FEE", nullable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal paymentFee;
    /**
     * 付款人
     */
    @Column(name = "PAYER", updatable = false)
    private String payer;
    /**
     * 备注
     */
    @Column(name = "MEMO", updatable = false, length = 3000)
    private String memo;
    /**
     * 支付状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "PAY_STATUS", nullable = false)
    private PaymentStatus status;
    /**
     * 支付方式
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAY_CONFIG_ID", foreignKey = @ForeignKey(name = "FK_PAYMENT_PAYMENT_CONFIG"))
    private PayConfig payConfig;
    /**
     * 订单详情
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PAYMENT_ORDER"))
    private Order order;
    /**
     * 交易记录
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TXN_SN", nullable = false, referencedColumnName = "SN", foreignKey = @ForeignKey(name = "FK_PAYMENT_TRANSACTION"))
    private Transaction transaction;
    /**
     * 支付时间
     */
    @Column(name = "TRADE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tradeTime;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public String getPayConfigName() {
        return payConfigName;
    }

    public void setPayConfigName(String payConfigName) {
        this.payConfigName = payConfigName;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
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

    public PayConfig getPayConfig() {
        return payConfig;
    }

    public void setPayConfig(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    @Transient
    public String getOrderId() {
        return this.getOrder().getId();
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Transient
    public String getTransactionId() {
        return this.getTransaction() != null ? this.getTransaction().getSn() : null;
    }

}