
package org.jfantasy.logistics.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.order.bean.OrderItem;

import javax.persistence.*;

/**
 * 物流项
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-22 上午10:53:37
 */
@Entity
@Table(name = "LOG_DELIVERY_ITEM")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class DeliveryItem extends BaseBusEntity {

    private static final long serialVersionUID = -6783787752984851646L;

    /**
     * 初始化物流项
     *
     * @param orderItem 订单项
     */
    public void initialize(OrderItem orderItem) {
        this.setSn(orderItem.getSn());
        this.setName(orderItem.getName());
    }

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "delivery_item_gen")
    @TableGenerator(name = "delivery_item_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "mall_delivery_item:id", valueColumnName = "gen_value")
    private Long id;
    @Column(name = "SN", updatable = false, nullable = false)
    private String sn;// 商品货号
    @Column(name = "NAME", updatable = false, nullable = false)
    private String name;// 商品名称
    @Column(name = "QUANTITY", updatable = false, nullable = false)
    private Integer quantity;// 物流数量
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOGISTICS_ID",foreignKey = @ForeignKey(name = "FK_DELIVERY_ITEM_LOGISTICS"))
    private Logistics logistics;// 物流

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Logistics getLogistics() {
        return logistics;
    }

    public void setLogistics(Logistics logistics) {
        this.logistics = logistics;
    }
}