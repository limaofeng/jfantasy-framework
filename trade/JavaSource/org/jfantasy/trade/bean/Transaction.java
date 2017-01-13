package org.jfantasy.trade.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.trade.bean.enums.TxChannel;
import org.jfantasy.trade.bean.enums.TxStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易表
 */
@Entity
@Table(name = "PAY_TRANSACTION")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "payments", "refunds", "union_id"})
public class Transaction extends BaseBusEntity {

    private static final long serialVersionUID = 3296031463173407900L;

    public static final String UNION_KEY = "union_key";

    public static final String STAGE = "stage";
    public static final String STAGE_PAYMENT = "stage_payment";
    public static final String STAGE_REFUND = "stage_refund";
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_TYPE = "order_type";
    public static final String CARD_ID = "card_id";

    /**
     * 交易流水号
     */
    @Id
    @Column(name = "SN", updatable = false, length = 32)
    @GeneratedValue(generator = "serialnumber")
    @GenericGenerator(name = "serialnumber", strategy = "serialnumber", parameters = {@org.hibernate.annotations.Parameter(name = "expression", value = "#DateUtil.format('yyyyMMdd') + #StringUtil.addZeroLeft(#SequenceInfo.nextValue('TX-SN' + #DateUtil.format('yyyyMMdd')), 5)")})
    private String sn;
    /**
     * 交易表唯一
     * 主要为了防止重复交易的发生
     * 格式为:projectKey|key
     */
    @Column(name = "UNION_ID", length = 32, updatable = false, unique = true)
    private String unionId;
    /**
     * 转出账号<br/> 充值时,可以为空
     */
    @Column(name = "FROM_ACCOUNT", length = 32)
    private String from;
    /**
     * 转入账号<br/>
     */
    @Column(name = "TO_ACCOUNT", length = 32)
    private String to;
    /**
     * 交易项目(转账/提现等)
     */
    @Column(name = "PROJECT", length = 20, updatable = false)
    private String project;
    /**
     * 交易科目
     */
    @Column(name = "SUBJECT", length = 20, updatable = false)
    private String subject;
    /**
     * 金额
     */
    @Column(name = "AMOUNT", nullable = false, updatable = false)
    private BigDecimal amount;
    /**
     * 交易渠道
     */
    @Column(name = "CHANNEL", length = 20)
    @Enumerated(EnumType.STRING)
    private TxChannel channel;
    /**
     * 交易状态
     */
    @Column(name = "STATUS", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TxStatus status;
    /**
     * 流程状态。
     * 提现: 未处理 (0)  -> 账户扣款 (1)  -> 转账中 (2)  -> 已完成 (9)  -> 关闭 (-1)
     * 充值: 未处理 (0)  -> 已充值 (1)  -> 完成 (9)  -> 关闭 (-1)
     * 订单: 未支付 (0)  -> 完成 (9)  -> 关闭 (-1)
     * 转账：未处理 (0)  -> 账户扣款 (1) -> 转账中 (2) -> 账户到账 (3) -> 已完成 (9) -> 关闭 (-1)
     */
    @Column(name = "FLOW_STATUS")
    private Integer flowStatus;
    /**
     * 状态文本
     */
    @Column(name = "STATUS_TEXT", length = 50, nullable = false)
    private String statusText;
    /**
     * 备注
     */
    @Column(name = "NOTES", length = 100)
    private String notes;
    /**
     * 支付配置名称
     */
    @Column(name = "PAYMENT_CONFIG_NAME", length = 50)
    private String payConfigName;
    /**
     * 扩展字段,用于存储不同项目的关联信息
     */
    @Convert(converter = MapConverter.class)
    @Column(name = "PROPERTIES", columnDefinition = "Text")
    private Map<String, Object> properties;//NOSONAR
    /**
     * 订单ID，与订单有关的业务时，该字段不为null
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", foreignKey = @ForeignKey(name = "FK_TRANSACTION_ORDER"))
    private Order order;
    /**
     * 支付记录
     **/
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<Payment> payments = new ArrayList<>();
    /**
     * 退款记录
     **/
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<Refund> refunds = new ArrayList<>();

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TxStatus getStatus() {
        return status;
    }

    public void setStatus(TxStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TxChannel getChannel() {
        return channel;
    }

    public void setChannel(TxChannel channel) {
        this.channel = channel;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
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

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void set(String key, String value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
    }

    @Transient
    public String get(String key) {
        if (this.properties == null || !this.properties.containsKey(key)) {
            return null;
        }
        return (String) this.properties.get(key);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public static String generateUnionid(String projectKey, String orderKey) {
        return projectKey + ">" + orderKey;
    }

    public String getPayConfigName() {
        return payConfigName;
    }

    public void setPayConfigName(String payConfigName) {
        this.payConfigName = payConfigName;
    }

    public Integer getFlowStatus() {
        return flowStatus;
    }

    public void setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
