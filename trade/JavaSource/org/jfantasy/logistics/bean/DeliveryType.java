package org.jfantasy.logistics.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.logistics.bean.databind.ExpressDeserializer;
import org.jfantasy.logistics.bean.enums.DeliveryMethod;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 配送方式
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-16 下午3:45:17
 */
@ApiModel("配送方式")
@Entity
@Table(name = "LOG_DELIVERY_TYPE")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "shippings", "reships", "corp"})
public class DeliveryType extends BaseBusEntity {

    private static final long serialVersionUID = 5873163245980853245L;

    public DeliveryType() {
    }

    public DeliveryType(Long id) {
        this.id = id;
    }

    @Id
    @Column(name = "ID", insertable = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "delivery_type_gen")
    @TableGenerator(name = "delivery_type_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "mall_delivery_type:id", valueColumnName = "gen_value")
    private Long id;
    /** 配送方式名称 **/
    @Column(name = "NAME", nullable = false)
    private String name;
    /** 配送类型 **/
    @Enumerated(EnumType.STRING)
    @Column(name = "DELIVERY_METHOD", length = 50, nullable = false)
    private DeliveryMethod method;
    @ApiModelProperty("首重量(单位: 克)")
    @Column(name = "first_Weight", nullable = false)
    private Integer firstWeight;
    @ApiModelProperty("续重量(单位: 克)")
    @Column(name = "CONTINUE_WEIGHT", nullable = false)
    private Integer continueWeight;
    /** 首重价格 **/
    @Column(name = "FIRST_WEIGHT_PRICE", precision = 15, scale = 5, nullable = false)
    private BigDecimal firstWeightPrice;
    /** 续重价格 **/
    @Column(name = "CONTINUE_WEIGHT_PRICE", precision = 15, scale = 5, nullable = false)
    private BigDecimal continueWeightPrice;
    /** 介绍 **/
    @Column(name = "DESCRIPTION", length = 3000)
    private String description;
    @ApiModelProperty(value = "默认物流公司")
    @JsonDeserialize(using = ExpressDeserializer.class)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "EXPRESS_ID", foreignKey = @ForeignKey(name = "TRADE_DELIVERY_TYPE_CORP"))
    private Express express;
    @ApiModelProperty(value = "发货", hidden = true)
    @OneToMany(mappedBy = "deliveryType", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Logistics> logisticss;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeliveryMethod getMethod() {
        return method;
    }

    public void setMethod(DeliveryMethod method) {
        this.method = method;
    }

    public Integer getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(Integer firstWeight) {
        this.firstWeight = firstWeight;
    }

    public Integer getContinueWeight() {
        return continueWeight;
    }

    public void setContinueWeight(Integer continueWeight) {
        this.continueWeight = continueWeight;
    }

    public BigDecimal getFirstWeightPrice() {
        return firstWeightPrice;
    }

    public void setFirstWeightPrice(BigDecimal firstWeightPrice) {
        this.firstWeightPrice = firstWeightPrice;
    }

    public BigDecimal getContinueWeightPrice() {
        return continueWeightPrice;
    }

    public void setContinueWeightPrice(BigDecimal continueWeightPrice) {
        this.continueWeightPrice = continueWeightPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Logistics> getLogisticss() {
        return logisticss;
    }

    public void setLogisticss(List<Logistics> logisticss) {
        this.logisticss = logisticss;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express defaultExpress) {
        this.express = express;
    }

}