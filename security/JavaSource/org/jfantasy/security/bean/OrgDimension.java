package org.jfantasy.security.bean;

import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.util.jackson.JSON;
import org.jfantasy.system.bean.Website;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * 组织维度
 */
@Entity
@Table(name = "AUTH_ORG_DIMENSION")
@JsonFilter(JSON.CUSTOM_FILTER)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","website"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OrgDimension extends BaseBusEntity {
    /**
     * 维度Id
     */
    @Id
    @Column(name = "CODE")
    private String id;
    /**
     * 维度名称
     */
    @Column(name = "NAME")
    private String name;
    /**
     * 维度描述
     */
    @Column(name = "DESCRIPTION")
    private String description;
    /**
     * 维度对应的站点
     */
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.REFRESH)
    @JoinColumn(name = "WEBSITE_KEY", foreignKey = @ForeignKey(name = "FK_AUTH_ORG_RELATION_WEBSITE"))
    private Website website;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Website getWebsite() {
        return website;
    }

    public void setWebsite(Website website) {
        this.website = website;
    }
}
