package org.jfantasy.trade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.trade.bean.enums.PointStatus;
import org.jfantasy.trade.bean.enums.PointType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PAY_POINT")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Point extends BaseBusEntity {

    private static final long serialVersionUID = -1042566187886657733L;

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(generator = "pay_point_gen")
    @TableGenerator(name = "pay_point_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "pay_point:id", valueColumnName = "gen_value")
    private Long id;
    /**
     * 积分类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, updatable = false)
    private PointType type;
    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private PointStatus status;
    /**
     * 积分数值
     */
    @Column(name = "POINT", nullable = false)
    private Long point;
    /**
     * 过期时间(如果有过期时间时必填)
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, name = "EXPIRE_TIME")
    private Date expire;
    /**
     * 备注
     */
    @Column(name = "NOTES")
    private String notes;
    /**
     * 关联账号
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_SN", nullable = false, foreignKey = @ForeignKey(name = "FK_POINT_ACCOUNT"))
    private Account account;

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public PointType getType() {
        return type;
    }

    public void setType(PointType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public PointStatus getStatus() {
        return status;
    }

    public void setStatus(PointStatus status) {
        this.status = status;
    }

}

