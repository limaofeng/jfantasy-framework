package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;

@Entity
@Table(name = "ORDER_PAYEE_VALUE")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "order", "payee", "creator", "create_time", "modifier", "modify_time"})
public class OrderPayeeValue extends BaseBusEntity {

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_payee_value_gen")
    @TableGenerator(name = "order_payee_value_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "order_payee_value:id", valueColumnName = "gen_value")
    private Long id;
    /**
     * 订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDERPAYEEVALUE_ORDER"))
    private Order order;
    /**
     * 收款人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_PAYEE", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ORDERPAYEEVALUE_ORDERPAYEE"))
    private OrderPayee payee;
    /**
     * 收款人对象标示。方便搜索
     */
    @Column(name = "TARGET", updatable = false,length = 20)
    private String target;
    /**
     * 收款人名称
     */
    @Column(name = "NAME", nullable = false,length = 50)
    private String name;
    /**
     * 收款人
     */
    @Column(name = "VALUE", nullable = false, updatable = false,length = 32)
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderPayee getPayee() {
        return payee;
    }

    public void setPayee(OrderPayee payee) {
        this.payee = payee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Transient
    public String getCode() {
        if (this.getPayee() == null) {
            return null;
        }
        return this.getPayee().getCode();
    }

    @Transient
    public String getTitle() {
        if (this.getPayee() == null) {
            return null;
        }
        return this.getPayee().getTitle();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
