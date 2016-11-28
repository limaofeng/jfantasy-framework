package org.jfantasy.card.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.order.bean.ExtraService;
import org.jfantasy.card.bean.converter.ExtraServiceConverter;
import org.jfantasy.card.bean.enums.CardStatus;
import org.jfantasy.card.bean.enums.Usage;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 消费卡
 */
@Entity
@Table(name = "PAY_CARD")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "owner", "version"})
public class Card extends BaseBusEntity {

    private static final long serialVersionUID = 7353590331858523890L;

    public static final String SUBJECT_BY_CARD_INPOUR = "CARD";

    /**
     * 卡号
     */
    @Id
    @Column(name = "NUMBER", length = 20)
    private String no;
    /**
     * 会员卡使用方式
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "_USAGE", length = 20, updatable = false)
    private Usage usage;
    /**
     * 会员卡类型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE", updatable = false, foreignKey = @ForeignKey(name = "FK_CARD_TYPE"))
    @JsonUnwrapped(prefix = "type_")
    @JsonIgnoreProperties({"description", "creator", "modifier", "create_time", "modify_time"})
    private CardType type;
    /**
     * 卡设计
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DESIGN", updatable = false, foreignKey = @ForeignKey(name = "FK_CARD_DESIGN"))
    @JsonUnwrapped(prefix = "design_")
    @JsonIgnoreProperties({"rule", "status", "amount", "notes", "extras", "card_type", "creator", "modifier", "create_time", "modify_time"})
    private CardDesign design;
    /**
     * 批次
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BATCH_ID", updatable = false, foreignKey = @ForeignKey(name = "FK_CARD_BATCH"))
    @JsonUnwrapped(prefix = "batch_")
    @JsonIgnoreProperties({"id", "status", "quantity", "status", "card_type", "card_design", "release_time", "release_notes", "creator", "modifier", "create_time", "modify_time"})
    private CardBatch batch;
    /**
     * 卡状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20)
    private CardStatus status;
    /**
     * 密钥
     */
    @Column(name = "SECRET", updatable = false, length = 10)
    private String secret;
    /**
     * 金额
     */
    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    /**
     * 销卡时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DESTROY_TIME")
    private Date destroyTime;
    /**
     * 附加服务
     */
    @Column(name = "EXTRAS", length = 1000)
    @Convert(converter = ExtraServiceConverter.class)
    private ExtraService[] extras;
    /**
     * 绑定账户
     */
    @Column(name = "ACCOUNT_SN", length = 20)
    private String account;
    /**
     * 所有者
     */
    @Column(name = "OWNER", length = 20)
    private String owner;
    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public CardBatch getBatch() {
        return batch;
    }

    public void setBatch(CardBatch batch) {
        this.batch = batch;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDestroyTime() {
        return destroyTime;
    }

    public void setDestroyTime(Date destroyTime) {
        this.destroyTime = destroyTime;
    }

    public CardDesign getDesign() {
        return design;
    }

    public ExtraService[] getExtras() {
        return extras;
    }

    public void setExtras(ExtraService[] extras) {
        this.extras = extras;
    }

    public void setDesign(CardDesign design) {
        this.design = design;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}