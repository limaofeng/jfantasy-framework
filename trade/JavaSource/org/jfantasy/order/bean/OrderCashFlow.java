package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.order.bean.databind.OrderCashFlowDeserializer;
import org.jfantasy.order.bean.databind.OrderCashFlowSerializer;
import org.jfantasy.order.bean.databind.OrderTypeDeserializer;
import org.jfantasy.order.bean.databind.OrderTypeSerializer;
import org.jfantasy.order.bean.enums.PayeeType;
import org.jfantasy.order.bean.enums.Stage;
import org.jfantasy.order.rest.models.ProfitChain;
import org.jfantasy.order.service.OrderTypeService;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 订单现金流
 */
@Entity
@Table(name = "ORDER_CASH_FLOW", uniqueConstraints = @UniqueConstraint(name = "UK_ORDERTYPE_CODE", columnNames = {"ORDER_TYPE", "CODE"}))
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "subflows"})
public class OrderCashFlow extends BaseBusEntity {

    private static OrderTypeService orderTypeService;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_cash_flow_gen")
    @TableGenerator(name = "order_cash_flow_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "order_cash_flow:id", valueColumnName = "gen_value")
    @Column(name = "ID", updatable = false)
    private Long id;
    /**
     * 在同一个 orderType 范围内，唯一。切不能修改
     */
    @Column(name = "CODE", length = 50, nullable = false)
    private String code;
    /**
     * 名称
     */
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    /**
     * 对应的项目
     */
    @Column(name = "PROJECT", length = 20, nullable = false, updatable = false)
    private String project;
    /**
     * 项目名称冗余
     */
    @Column(name = "PROJECT_NAME", length = 50, nullable = false, updatable = false)
    private String projectName;
    /**
     * 阶段
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STAGE", length = 10, nullable = false)
    private Stage stage;
    /**
     * 收款金额表达式
     */
    @Column(name = "VALUE", length = 200, nullable = false)
    private String value;
    /**
     * 收款人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYEE", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDERCASHFLOW_PAYEE"))
    private OrderPayee payee;
    /**
     * 备注
     */
    @Column(name = "NOTES", length = 50)
    private String notes;
    /**
     * 层级
     */
    @Column(name = "LAYER", nullable = false)
    private Integer layer;
    /**
     * 排序字段
     */
    @Column(name = "SORT")
    private Integer sort;
    /**
     * 订单配置
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = OrderTypeSerializer.class)
    @JsonDeserialize(using = OrderTypeDeserializer.class)
    @JoinColumn(name = "ORDER_TYPE", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ORDERCASHFLOW_ORDERTYPE"))
    private OrderType orderType;
    /**
     * 上级菜单
     */
    @JsonProperty("parentId")
    @JsonBackReference
    @JsonSerialize(using = OrderCashFlowSerializer.class)
    @JsonDeserialize(using = OrderCashFlowDeserializer.class)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "PID", updatable = false, foreignKey = @ForeignKey(name = "FK_ORDER_CASH_FLOW_PID"))
    private OrderCashFlow parent;
    /**
     * 子流程
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @OrderBy("sort ASC")
    private List<OrderCashFlow> subflows;

    @Transient
    private ProfitChain profitChain;

    public OrderCashFlow() {
    }

    public OrderCashFlow(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OrderPayee getPayee() {
        return payee;
    }

    public void setPayee(OrderPayee payee) {
        this.payee = payee;
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

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public static OrderTypeService getOrderTypeService() {
        return orderTypeService;
    }

    public static void setOrderTypeService(OrderTypeService orderTypeService) {
        OrderCashFlow.orderTypeService = orderTypeService;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<OrderCashFlow> getSubflows() {
        return subflows;
    }

    public void setSubflows(List<OrderCashFlow> subflows) {
        this.subflows = subflows;
    }

    public OrderCashFlow getParent() {
        return parent;
    }

    public void setParent(OrderCashFlow parent) {
        this.parent = parent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Transient
    public BigDecimal getValue(Order order) {
        return orderTypeService().getValue(this, order).setScale(2, RoundingMode.DOWN);
    }

    @Transient
    public String getPayee(Order order) {
        return orderTypeService().getPayee(this, order);
    }

    @Transient
    private static OrderTypeService orderTypeService() {
        if (orderTypeService == null) {
            orderTypeService = SpringContextUtil.getBeanByType(OrderTypeService.class);
        }
        return orderTypeService;
    }

    @Transient
    public ProfitChain getProfitChain(Order order) {
        if (profitChain == null) {
            profitChain = this.getProfitChain();
            BigDecimal revenue = this.getValue(order);
            if (payee.getType() == PayeeType.fixed) {
                profitChain.setPayee(payee.getCode());
                profitChain.setName(payee.getTitle());
            } else {
                OrderPayeeValue payeeValue = ObjectUtil.find(order.getPayees(), "code", this.getPayee().getCode());
                profitChain.setPayee(payeeValue.getValue());
                profitChain.setName(payeeValue.getName());
            }
            profitChain.setRevenue(revenue);
            profitChain.setBalance(revenue);
        }
        return profitChain;
    }

    @Transient
    public ProfitChain getProfitChain() {
        if (profitChain == null) {
            profitChain = new ProfitChain();
            profitChain.setId(this.code);
            profitChain.setType(this.getPayee().getType());
            profitChain.setRole(this.getPayee().getCode());
            profitChain.setRoleName(this.getPayee().getTitle());
            profitChain.setNotes(this.getNotes());
            profitChain.setProject(this.getProject());
            profitChain.setProjectName(this.getProjectName());
        }
        return profitChain;
    }

}
