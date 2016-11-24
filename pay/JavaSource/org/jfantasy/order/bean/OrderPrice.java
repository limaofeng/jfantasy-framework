package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "TRADE_ORDER_PRICE")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "order"})
public class OrderPrice extends BaseBusEntity {

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_price_gen")
    @TableGenerator(name = "order_price_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "mall_order_price:id", valueColumnName = "gen_value")
    private Long id;
    /**
     * 编码
     */
    @Column(name = "CODE", updatable = false, length = 20)
    private String code;
    /**
     * 名称
     */
    @Column(name = "NAME", updatable = false, length = 20)
    private String name;
    /**
     * 金额
     */
    @Column(name = "AMOUNT", updatable = false, scale = 2)
    private BigDecimal amount;
    /**
     * 订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_PRICE"))
    private Order order;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
