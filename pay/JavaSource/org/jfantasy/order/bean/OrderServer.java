package org.jfantasy.order.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册的订单服务器
 */
@Entity
@Table(name = "PAY_ORDER_SERVER")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class OrderServer extends BaseBusEntity {

    /**
     * 主机地址<br/>
     */
    public static final String PROPS_HOST = "host";
    /**
     * 主机端口<br/>
     */
    public static final String PROPS_PORT = "port";
    /**
     * 调用服务时,需要提供的身份信息
     */
    public static final String PROPS_TOKEN = "token";

    private static final long serialVersionUID = 1314573695371668316L;
    /**
     * 服务的订单类型
     */
    @Id
    @Column(name = "TYPE", updatable = false, length = 50)
    private String type;
    /**
     * 服务名称
     */
    @Column(name = "NAME", length = 50)
    private String name;
    /**
     * 配置参数
     */
    @Convert(converter = MapConverter.class)
    @Column(name = "PROPERTIES", columnDefinition = "Text")
    private Map<String, Object> properties;//NOSONAR
    /**
     * 详细介绍
     */
    @Column(name = "DESCRIPTION", length = 3000)
    private String description;
    /**
     * 是否启用
     */
    @Column(name = "ENABLED")
    private boolean enabled;

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void set(String key, String value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
    }

    @Transient
    public String get(String key) {
        if (this.properties == null || !this.properties.containsKey(key)) {
            return null;
        }
        return this.properties.get(key).toString();
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
