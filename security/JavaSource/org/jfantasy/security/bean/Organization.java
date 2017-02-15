package org.jfantasy.security.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.security.bean.databind.OrgDeserializer;
import org.jfantasy.security.bean.databind.OrgSerializer;

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
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "jobs", "children","employees"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Organization extends BaseBusEntity {

    private static final long serialVersionUID = -6159187521342750200L;

    /**
     * 组织机构类型
     */
    public enum OrgType {
        /**
         * 企业
         */
        enterprise,
        /**
         * 部门
         */
        department
    }

    /**
     * 机构简写
     */
    @Id
    @Column(name = "CODE", length = 10)
    private String id;
    /**
     * 机构名称
     */
    @Column(name = "NAME", length = 50)
    private String name;
    /**
     * 机构类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 10)
    private OrgType type;
    /**
     * 排序字段
     */
    @Column(name = "SORT")
    private Integer sort;
    /**
     * 机构描述信息
     */
    @Column(name = "DESCRIPTION", length = 150)
    private String description;
    /**
     * 上级机构
     */
    @JsonProperty("parent_id")
    @JsonSerialize(using = OrgSerializer.class)
    @JsonDeserialize(using = OrgDeserializer.class)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "PID", foreignKey = @ForeignKey(name = "FK_AUTH_ORGANIZATION_PID"))
    private Organization parent;
    /**
     * 下属机构
     */
    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @OrderBy("sort ASC")
    private List<Organization> children;
    /**
     * 对于的岗位
     */
    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<Job> jobs;
    /**
     * 用户
     */
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<Employee> employees;

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

    public Organization getParent() {
        return parent;
    }

    public void setParent(Organization parent) {
        this.parent = parent;
    }

    public OrgType getType() {
        return type;
    }

    public void setType(OrgType type) {
        this.type = type;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
