package org.jfantasy.pay.bean;

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
    @Column(name = "ID", length = 50, nullable = false)
    private String name;
    /**
     * 订单未支付时的有效期，单位为分钟。超过规定时间后，自动关闭
     */
    @NotNull(groups = RESTful.POST.class)
    @Column(name = "ID", length = 50, nullable = false)
    private Long expires;
    /**
     * 是否启用
     */
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

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

}
