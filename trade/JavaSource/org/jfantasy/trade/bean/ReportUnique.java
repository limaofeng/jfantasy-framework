package org.jfantasy.trade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.trade.bean.enums.ReportTargetType;

import javax.persistence.*;

/**
 * 用于防止重复统计
 */
@Entity
@Table(name = "TRADE_REPORT_UNIQUE", uniqueConstraints = {@UniqueConstraint(columnNames = {"KEY", "TAG"})})
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class ReportUnique extends BaseBusEntity {

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(generator = "pay_report_unique_gen")
    @TableGenerator(name = "pay_report_unique_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "pay_report_unique:id", valueColumnName = "gen_value")
    private Long id;
    /**
     * 类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TARGET_TYPE", length = 20, updatable = false, nullable = false)
    private ReportTargetType targetType;
    /**
     * 对应 accountId
     */
    @Column(name = "TARGET_ID", length = 32, updatable = false, nullable = false)
    private String targetId;
    /**
     *
     */
    @Column(name = "KEY", length = 50, updatable = false, nullable = false)
    private String key;
    /**
     * 标记
     */
    @Column(name = "TAG", length = 20, updatable = false, nullable = false)
    private String tag;

    public ReportUnique() {
    }

    public ReportUnique(ReportTargetType targetType, String targetId, String key, String tag) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.key = key;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(ReportTargetType targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
