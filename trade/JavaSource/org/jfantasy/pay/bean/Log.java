package org.jfantasy.pay.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.pay.bean.enums.OwnerType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PAY_LOG", uniqueConstraints = {@UniqueConstraint(columnNames = {"OWNER_TYPE", "OWNER_ID"})})
@TableGenerator(name = "log_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "pay_log:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "creator", "createTime", "modifier", "modifyTime"})
public class Log extends BaseBusEntity {

    private static final long serialVersionUID = 5304664083237571141L;

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "log_gen")
    private Long id;
    /**
     * 所有者类型
     */
    @Column(name = "OWNER_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;
    /**
     * 所有者id
     */
    @Column(name = "OWNER_ID", updatable = false, length = 30)
    private String ownerId;
    /**
     * 订单ID
     */
    @Column(name = "ORDER_KEY", updatable = false, length = 30)
    private String orderKey;
    /**
     * 动作
     */
    @Column(name = "ACTION", nullable = false, updatable = false)
    private String action;
    /**
     * 备注
     */
    @Column(name = "NOTES", updatable = false)
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OwnerType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getLogTime() {
        return this.getCreateTime();
    }

}
