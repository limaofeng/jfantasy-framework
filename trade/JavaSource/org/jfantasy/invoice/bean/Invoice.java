package org.jfantasy.invoice.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.jfantasy.common.Area;
import org.jfantasy.common.converter.AreaConverter;
import org.jfantasy.common.databind.AreaDeserializer;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.invoice.bean.enums.InvoiceStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.List;

/**
 * 发票管理
 */
@Entity
@Table(name = "MEM_INVOICE")
@TableGenerator(name = "invoice_gen", table = "sys_sequence",pkColumnName = "gen_name",pkColumnValue = "mem_invoice:id",valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Invoice extends BaseBusEntity {

    private static final long serialVersionUID = -4779393716963955860L;
    @Null(groups = {RESTful.POST.class})
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "invoice_gen")
    @Column(name = "ID", updatable = false)
    private Long id;
    @GeneratedValue(generator = "serialnumber")
    @GenericGenerator(
            name = "serialnumber",
            strategy = "serialnumber",
            parameters = {
                    @Parameter(
                            name = "expression",
                            value = "#DateUtil.format('yyyyMMdd') + #StringUtil.addZeroLeft(#SequenceInfo.nextValue('INVOICE-NO' + #DateUtil.format('yyyyMMdd')), 5)"
                    )})
    @Column(name = "NO", nullable = false, length = 20)
    private String no;
    @Column(name = "TYPE", length = 10)
    private String type;
    @Null(groups = {RESTful.POST.class})
    @Column(name = "STATUS", length = 10)
    private InvoiceStatus status;
    /*************************************/
    /*          发票内容                  */
    /*************************************/
    @Column(name = "content", length = 200)
    private String content;
    @NotNull(groups = {RESTful.POST.class})
    @Column(name = "TITLE", length = 200)
    private String title;
    @Null(groups = {RESTful.POST.class})
    @Column(name = "AMOUNT", scale = 2, nullable = false)
    private BigDecimal amount;
    /*************************************/
    /*          物流信息                  */
    /*************************************/
    @Column(name = "LOGISTICS", length = 20)
    private String logistics;
    @JsonProperty("ship_no")
    @Column(name = "SHIP_NO", length = 20)
    private String shipNo;
    /*************************************/
    /*          收件人信息                */
    /*************************************/
    @NotNull(groups = {RESTful.POST.class})
    @JsonProperty("ship_name")
    @Column(name = "SHIP_NAME", nullable = false)
    private String shipName;
    @NotNull(groups = {RESTful.POST.class})
    @JsonProperty("ship_tel")
    @Column(name = "SHIP_TEL", nullable = false)
    private String shipTel;
    @NotNull(groups = {RESTful.POST.class})
    @Column(name = "SHIP_AREA_STORE", nullable = false)
    @Convert(converter = AreaConverter.class)
    @JsonDeserialize(using = AreaDeserializer.class)
    private Area area;
    @NotNull(groups = {RESTful.POST.class})
    @JsonProperty("ship_address")
    @Column(name = "SHIP_ADDRESS", nullable = false)
    private String shipAddress;// 收货地址
    @JsonProperty("ship_zip_code")
    @Column(name = "SHIP_ZIP_CODE")
    private String shipZipCode;// 收货邮编
    /*************************************/
    /*             开票项目               */
    /*************************************/
    @NotNull(groups = {RESTful.POST.class})
    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<InvoiceItem> items;
    /*************************************/
    /*             申请人                */
    /*************************************/
    @NotNull(groups = {RESTful.POST.class})
    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;
    /*************************************/
    /*             开票人                */
    /*************************************/
    @Column(name = "DRAWER", nullable = false)
    private Long drawer;

    public Invoice() {
    }

    public Invoice(Long id) {
        this.setId(id);
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getLogistics() {
        return logistics;
    }

    public void setLogistics(String logistics) {
        this.logistics = logistics;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipTel() {
        return shipTel;
    }

    public void setShipTel(String shipTel) {
        this.shipTel = shipTel;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getDrawer() {
        return drawer;
    }

    public void setDrawer(Long drawer) {
        this.drawer = drawer;
    }

}
