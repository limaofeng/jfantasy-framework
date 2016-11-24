package org.jfantasy.trade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.trade.bean.converter.ReportItemsConverter;
import org.jfantasy.trade.bean.enums.BillType;
import org.jfantasy.trade.bean.enums.ReportTargetType;
import org.jfantasy.trade.bean.enums.TimeUnit;

import javax.persistence.*;

/**
 * 统计
 */
@Entity
@Table(name = "PAY_REPORT", uniqueConstraints = {@UniqueConstraint(columnNames = {"TARGET_TYPE", "TARGET_ID", "TIME_UNIT", "TIME"})})
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler","version"})
public class Report extends BaseBusEntity {

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(generator = "pay_report_gen")
    @TableGenerator(name = "pay_report_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "pay_report:id", valueColumnName = "gen_value")
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
     * 可能的值<br/>
     * 时间 格式说明：<br/>
     * TimeUnit.day = 20130103 <br/>
     * TimeUnit.week = 201321 <br/>
     * TimeUnit.month = 201312 <br/>
     * TimeUnit.year = 2013
     * TimeUnit.all = longtime
     */
    @Column(name = "TIME", length = 8, updatable = false, nullable = false)
    private String time;
    /**
     * 时间单位
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TIME_UNIT", length = 8, updatable = false, nullable = false)
    private TimeUnit timeUnit;
    @Convert(converter = ReportItemsConverter.class)
    @Column(name = "items", columnDefinition = "Text")
    private ReportItem[] items;
    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void addItem(ReportItem item) {
        if (this.items == null) {
            this.items = new ReportItem[0];
        }
        this.items = ObjectUtil.join(this.items, item);
    }

    public ReportItem[] getItems() {
        return items;
    }

    public void setItems(ReportItem[] items) {
        this.items = items;
    }

    public ReportItem getItems(BillType type, String code) {
        if (this.items == null) {
            this.items = new ReportItem[0];
        }
        return ObjectUtil.find(items, item -> item.getType().equals(type) && item.getCode().equals(code));
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
