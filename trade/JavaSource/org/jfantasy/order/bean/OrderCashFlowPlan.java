package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.order.bean.databind.OrderTypeDeserializer;
import org.jfantasy.order.bean.databind.OrderTypeSerializer;

import javax.persistence.*;

/**
 * 订单现金流方案
 */
@Entity
@Table(name = "ORDER_CASHFLOW_PLAN")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "subflows"})
public class OrderCashFlowPlan extends BaseBusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_cashflow_plan_gen")
    @TableGenerator(name = "order_cashflow_plan_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "order_cashflow_plan:id", valueColumnName = "gen_value")
    @Column(name = "ID", updatable = false)
    private Long id;
    /**
     * 方案编码
     */
    @Column(name = "CODE", length = 50, nullable = false)
    private String code;
    /**
     * 方案名称
     */
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    /**
     * 备注
     */
    @Column(name = "NOTES", length = 50)
    private String notes;
    /**
     * 订单配置
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = OrderTypeSerializer.class)
    @JsonDeserialize(using = OrderTypeDeserializer.class)
    @JoinColumn(name = "ORDER_TYPE", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ORDERCASHFLOWPLAN_ORDERTYPE"))
    private OrderType orderType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
}
