package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.order.bean.databind.OrderTypeDeserializer;
import org.jfantasy.order.bean.databind.OrderTypeSerializer;
import org.jfantasy.order.bean.enums.DataType;
import org.jfantasy.order.bean.enums.PayeeType;

import javax.persistence.*;

@Entity
@Table(name = "ORDER_PAYEE", uniqueConstraints = @UniqueConstraint(name = "UK_ORDER_PAYEE", columnNames = {"CODE", "ORDER_TYPE"}))
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class OrderPayee extends BaseBusEntity {

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_payee_gen")
    @TableGenerator(name = "order_payee_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "order_payee:id", valueColumnName = "gen_value")
    private Long id;
    /***
     * 数据类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DATA_TYPE", nullable = false, updatable = false,length = 20)
    private DataType dataType;
    /**
     * 数据类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 20, nullable = false, updatable = false)
    private PayeeType type;
    /**
     * 编码
     */
    @Column(name = "CODE", updatable = false, nullable = false, length = 20)
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
    @JoinColumn(name = "REFERENCE", updatable = false,foreignKey = @ForeignKey(name = "FK_ORDERPAYEE_REFERENCE"))
    private OrderPayee reference;
    /**
     * 订单配置
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = OrderTypeSerializer.class)
    @JsonDeserialize(using = OrderTypeDeserializer.class)
    @JoinColumn(name = "ORDER_TYPE", updatable = false, foreignKey = @ForeignKey(name = "FK_ORDERPAYEE_ORDERTYPE"))
    private OrderType orderType;
    /**
     * 备注
     */
    @Column(name = "NOTES", length = 50)
    private String notes;

    public OrderPayee() {
    }

    public OrderPayee(OrderPayee reference) {
        if (reference.getDataType() == DataType.share) {
            this.dataType = DataType.reference;
        }
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PayeeType getType() {
        return type;
    }

    public void setType(PayeeType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OrderPayee getReference() {
        return reference;
    }

    public void setReference(OrderPayee reference) {
        this.reference = reference;
    }
}
