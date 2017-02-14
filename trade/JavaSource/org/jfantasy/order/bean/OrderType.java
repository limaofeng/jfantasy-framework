package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.spring.validation.RESTful;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 订单类型
 */
@Entity
@Table(name = "PAY_ORDER_TYPE")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class OrderType extends BaseBusEntity {

    /**
     * 类型ID
     */
    @NotEmpty(groups = RESTful.POST.class)
    @Id
    @Column(name = "ID", length = 20, nullable = false)
    private String id;
    /**
     * 订单名称
     */
    @NotEmpty(groups = RESTful.POST.class)
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    /**
     * 订单未支付时的有效期，单位为分钟。超过规定时间后，自动关闭
     */
    @NotNull(groups = RESTful.POST.class)
    @Column(name = "EXPIRES", length = 50, nullable = false)
    private Long expires;
    /**
     * 是否启用
     */
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;
    /**
     * 是否允许退款
     @Column(name = "ENABLED", nullable = false)
     private Boolean refundable;
     */
    /**
     * 摘要模版
     */
    @Column(name = "SUBJECT_TEMPLATE", nullable = false, length = 100)
    private String subject;
    /**
     * 正文模版
     */
    @Column(name = "BODY_TEMPLATE", nullable = false, length = 500)
    private String body;
    /**
     * 业务数据重定向URL
     */
    @Column(name = "REDIRECT_URL", nullable = false, length = 100)
    private String redirectUrl;
    /**
     * 发票开票方，编码
     */
    @Column(name = "DRAWER", length = 100)
    private String drawer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getDrawer() {
        return drawer;
    }

    public void setDrawer(String drawer) {
        this.drawer = drawer;
    }

}
