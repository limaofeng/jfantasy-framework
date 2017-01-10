package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.order.bean.databind.OrderTypeDeserializer;
import org.jfantasy.order.bean.databind.OrderTypeSerializer;
import org.jfantasy.order.bean.enums.DataType;

import javax.persistence.*;

@Entity
@Table(name = "ORDER_PRICE", uniqueConstraints = @UniqueConstraint(name = "UK_ORDER_PRICE", columnNames = {"CODE", "ORDER_TYPE"}))
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class OrderPrice extends BaseBusEntity {

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_price_gen")
    @TableGenerator(name = "order_price_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "order_price:id", valueColumnName = "gen_value")
    private Long id;
    /***
     * 数据类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DATA_TYPE", nullable = false, updatable = false)
    private DataType dataType;
    /**
     * 编码
     */
    @Column(name = "CODE", nullable = false, updatable = false, length = 20)
    private String code;
    /**
     * 名称
     */
    @Column(name = "TITLE", length = 20)
    private String title;
    /**
     * type = share 时，reference 不能为空
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFERENCE", updatable = false,foreignKey = @ForeignKey(name = "FK_ORDERPRICE_REFERENCE"))
    private OrderPrice reference;
    /**
     * 订单配置
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = OrderTypeSerializer.class)
    @JsonDeserialize(using = OrderTypeDeserializer.class)
    @JoinColumn(name = "ORDER_TYPE", updatable = false, foreignKey = @ForeignKey(name = "FK_ORDERPRICE_ORDERTYPE"))
    private OrderType orderType;
    /**
     * 备注
     */
    @Column(name = "NOTES", length = 50)
    private String notes;

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

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderPrice getReference() {
        return reference;
    }

    public void setReference(OrderPrice reference) {
        this.reference = reference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
