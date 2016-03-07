package org.jfantasy.security.bean;

import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.util.jackson.JSON;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

/**
 * 组织机构
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-22 下午04:00:57
 */
@Entity
@Table(name = "AUTH_ORGANIZATION")
@JsonFilter(JSON.CUSTOM_FILTER)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "jobs","orgHelpBeans"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Organization extends BaseBusEntity {

    private static final long serialVersionUID = -6159187521342750200L;

    /**
     * 组织机构类型
     */
    public enum OrgType {
        /**
         * 企业
         enterprise,*/
        /**
         * 公司
         */
        company,
        /**
         * 部门
         */
        department
    }

    /**
     * 机构简写
     */
    @Id
    @Column(name = "CODE")
    private String id;
    /**
     * 机构名称
     */
    @Column(name = "NAME")
    private String name;
    /**
     * 机构类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private OrgType type;
    /**
     * 机构描述信息
     */
    @Column(name = "DESCRIPTION")
    private String description;
    /**
     * 上级机构
     */
    @Transient
    private Organization parentOrganization;
    /**
     * 下属机构
     */
    @Transient
    private List<Organization> children;
    /**
     * 维度与上级机构
     */
    @Transient
    private List<OrgHelpBean> orgHelpBeans;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<Job> jobs;

    /**
     * 用户
     */
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<User> userList;


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

    public List<Organization> getChildren() {
        return children;
    }

    public void setChildren(List<Organization> children) {
        this.children = children;
    }

    public Organization getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(Organization parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public OrgType getType() {
        return type;
    }

    public void setType(OrgType type) {
        this.type = type;
    }

    public List<OrgHelpBean> getOrgHelpBeans() {
        return orgHelpBeans;
    }

    public void setOrgHelpBeans(List<OrgHelpBean> orgHelpBeans) {
        this.orgHelpBeans = orgHelpBeans;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
