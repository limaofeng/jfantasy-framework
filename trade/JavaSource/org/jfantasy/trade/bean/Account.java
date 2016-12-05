package org.jfantasy.trade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.trade.bean.enums.AccountStatus;
import org.jfantasy.trade.bean.enums.AccountType;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PAY_ACCOUNT")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "password", "version"})
public class Account extends BaseBusEntity {

    private static final long serialVersionUID = -1002874805597096558L;

    /**
     * 编号
     */
    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(generator = "serialnumber")
    @GenericGenerator(name = "serialnumber", strategy = "serialnumber", parameters = {@Parameter(name = "expression", value = "#DateUtil.format('yyyyMMdd') + #StringUtil.addZeroLeft(#SequenceInfo.nextValue('PAY-ACCOUNT-SN' + #DateUtil.format('yyyyMMdd')), 8)")})
    private String sn;
    /**
     * 账号类型
     */
    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private AccountType type;
    /**
     * 账户状态
     */
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    /**
     * 账户余额
     */
    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    /**
     * 支付密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "PASSWORD", length = 100)
    private String password;
    /**
     * 可用积分
     */
    @Column(name = "POINTS", nullable = false)
    private Long points;
    /**
     * 所有者
     */
    @Column(name = "OWNER", nullable = false, updatable = false)
    private String owner;
    /**
     * 所有者ID
     */
    @Column(name = "OWNER_ID", nullable = false, updatable = false)
    private String ownerId;
    /**
     * 所有者类型
     */
    @Column(name = "OWNER_TYPE", nullable = false, updatable = false)
    private String ownerType;
    /**
     * 所有者名称
     */
    @Column(name = "OWNER_NAME", nullable = false)
    private String ownerName;
    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Integer getVersion() {
        return version;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

}
