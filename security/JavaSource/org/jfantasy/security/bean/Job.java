package org.jfantasy.security.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;
import java.util.List;

/**
 * 岗位
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-22 下午04:00:48
 */
@Entity
@Table(name = "AUTH_JOB")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Job extends BaseBusEntity {

    private static final long serialVersionUID = -7020427994563623645L;

    /**
     * 岗位编码
     */
    @Id
    @Column(name = "CODE", length = 32)
    private String id;
    /**
     * 岗位名称
     */
    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    /**
     * 岗位描述信息
     */
    @Column(name = "DESCRIPTION", length = 250)
    private String description;
    /**
     * 角色关联
     */
    @ManyToMany(targetEntity = Role.class, fetch = FetchType.LAZY)
    @JoinTable(name = "AUTH_ROLE_JOB", joinColumns = @JoinColumn(name = "JOB_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_CODE"), foreignKey = @ForeignKey(name = "FK_ROLE_JOB_JID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Role> roles;
    /**
     * 组织机构
     */
    @ManyToOne
    @JoinColumn(name = "ORGANIZATION_ID", foreignKey = @ForeignKey(name = "FK_JOB_ORGANIZATION"))
    private Organization organization;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setOrgId(String id) {
        this.organization = new Organization();
        this.organization.setId(id);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
