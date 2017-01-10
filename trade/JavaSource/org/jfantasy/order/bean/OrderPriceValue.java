package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_PRICE_VALUE")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "order", "price", "creator", "create_time", "modifier", "modify_time"})
public class OrderPriceValue extends BaseBusEntity {

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_price_value_gen")
    @TableGenerator(name = "order_price_value_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "order_price_value:id", valueColumnName = "gen_value")
    private Long id;
    /**
     * 订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDERPRICEVALUE_ORDER"))
    private Order order;
    /**
     * 价格条目
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_PRICE", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ORDERPRICEVALUE_ORDERPRICE"))
    private OrderPrice price;
    /**
     * 金额
     */
    @Column(name = "VALUE", nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

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

    public OrderPrice getPrice() {
        return price;
    }

    public void setPrice(OrderPrice price) {
        this.price = price;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Transient
    public String getCode() {
        if (this.getPrice() == null) {
            return null;
        }
        return this.getPrice().getCode();
    }

    @Transient
    public String getTitle() {
        if (this.getPrice() == null) {
            return null;
        }
        return this.getPrice().getTitle();
    }

}
