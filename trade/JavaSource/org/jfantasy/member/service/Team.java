package org.jfantasy.member.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.jfantasy.framework.dao.BaseBusEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 团队/小组
 */
public class Team extends BaseBusEntity {

    /**
     * 团队
     */
    private String key;
    /**
     * 团队类型
     */
    private String type;
    /**
     * 状态
     */
    private String status;
    /**
     * 名称
     */
    private String name;
    /**
     * 集团所有者
     */
    private Long ownerId;
    /**
     * 描述
     */
    private String description;
    /**
     * 扩展属性
     */
    private Map<String, Object> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        if (value == null) {
            return;
        }
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

}
