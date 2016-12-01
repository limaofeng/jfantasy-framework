package org.jfantasy.logistics.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.jfantasy.common.Area;
import org.jfantasy.common.converter.AreaConverter;
import org.jfantasy.common.databind.AreaDeserializer;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.logistics.bean.databind.DeliveryTypeDeserializer;
import org.jfantasy.logistics.bean.databind.DeliveryTypeSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 物流信息
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-10-15 下午3:37:40
 */
@Entity
@Table(name = "LOG_LOGISTICS")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "items", "type"})
public class Logistics extends BaseBusEntity {

    private static final long serialVersionUID = 4315245804828793329L;
    @Id
    @Column(name = "SN", nullable = false, unique = true)
    @GenericGenerator(name = "serialnumber", strategy = "serialnumber", parameters = {@Parameter(name = "expression", value = "'SN_' + #DateUtil.format('yyyyMMdd') + #StringUtil.addZeroLeft(#SequenceInfo.nextValue('SHIPPING-SN'), 5)")})
    private String sn;//发货编号
    @JsonProperty("typeName")
    @Column(name = "DELIVERY_TYPE_NAME", length = 50)
    private String deliveryTypeName;//配送方式名称
    @JsonProperty("corpName")
    @Column(name = "DELIVERY_CORP_NAME", length = 50)
    private String deliveryCorpName;//物流公司名称
    @JsonProperty("corpURL")
    @Column(name = "DELIVERY_CORP_URL", length = 50)
    private String deliveryCorpUrl;//物流公司网址
    @Column(name = "DELIVERY_SN", length = 50)
    private String deliverySn;//物流单号
    @Column(name = "DELIVERY_FEE", precision = 10, scale = 2)
    private BigDecimal deliveryFee;//物流费用
    @Column(name = "SHIP_NAME", length = 50)
    private String shipName;//收货人姓名
    @Column(name = "SHIP_AREA_STORE", length = 300)
    @Convert(converter = AreaConverter.class)
    private Area shipArea;//收货地区信息
    @Column(name = "SHIP_ADDRESS", length = 150)
    private String shipAddress;//收货地址
    @Column(name = "SHIP_ZIP_CODE", length = 10)
    private String shipZipCode;//收货邮编
    @Column(name = "SHIP_MOBILE", length = 12)
    private String shipMobile;//收货手机
    @Column(name = "MEMO", length = 150)
    private String memo;//备注
    @Column(name = "ORDER_ID")
    private String orderId;//订单编号
    /**
     * 配送方式
     */
    @JsonProperty("type")
    @JsonSerialize(using = DeliveryTypeSerializer.class)
    @JsonDeserialize(using = DeliveryTypeDeserializer.class)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "DELIVERY_TYPE_ID", foreignKey = @ForeignKey(name = "FK_LOGISTICS_DELIVERY_TYPE"))
    private DeliveryType deliveryType;
    /**
     * 物流项
     */
    @ApiModelProperty(name = "items", hidden = true)
    @JsonProperty("items")
    @OneToMany(mappedBy = "logistics", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<DeliveryItem> deliveryItems = new ArrayList<>();

    public String getDeliveryTypeName() {
        return deliveryTypeName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setDeliveryTypeName(String deliveryTypeName) {
        this.deliveryTypeName = deliveryTypeName;
    }

    public String getDeliveryCorpName() {
        return deliveryCorpName;
    }

    public void setDeliveryCorpName(String deliveryCorpName) {
        this.deliveryCorpName = deliveryCorpName;
    }

    public String getDeliveryCorpUrl() {
        return deliveryCorpUrl;
    }

    public void setDeliveryCorpUrl(String deliveryCorpUrl) {
        this.deliveryCorpUrl = deliveryCorpUrl;
    }

    public String getDeliverySn() {
        return deliverySn;
    }

    public void setDeliverySn(String deliverySn) {
        this.deliverySn = deliverySn;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getShipZipCode() {
        return shipZipCode;
    }

    public void setShipZipCode(String shipZipCode) {
        this.shipZipCode = shipZipCode;
    }

    public String getShipMobile() {
        return shipMobile;
    }

    public void setShipMobile(String shipMobile) {
        this.shipMobile = shipMobile;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public List<DeliveryItem> getDeliveryItems() {
        return deliveryItems;
    }

    public void setDeliveryItems(List<DeliveryItem> deliveryItems) {
        this.deliveryItems = deliveryItems;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Area getShipArea() {
        return this.shipArea;
    }

    @JsonDeserialize(using = AreaDeserializer.class)
    public void setShipArea(Area shipArea) {
        this.shipArea = shipArea;
    }

}