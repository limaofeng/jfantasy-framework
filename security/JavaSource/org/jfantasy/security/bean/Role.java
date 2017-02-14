package org.jfantasy.security.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.util.common.StringUtil;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "AUTH_ROLE")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "menus", "permissions", "users", "jobs", "members", "roleAuthorities"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends BaseBusEntity {

    private static final long serialVersionUID = 4870450046611332600L;

    /**
     * 角色编码
     */
    @Id
    @Column(name = "CODE",length = 32)
    private String id;
    /**
     * 角色名称
     */
    @Column(name = "NAME",length = 50)
    private String name;
    /**
     * 角色类型，用于区分不同类型的角色。比如：后台管理与前台会员之间的角色
     */
    @Column(name = "TYPE", length = 20)
    private String type;
    /**
     * 是否启用 0禁用 1 启用
     */
    @Column(name = "ENABLED")
    private Boolean enabled;
    /**
     * 描述信息
     */
    @Column(name = "DESCRIPTION",length = 250)
    private String description;
    /**
     * 角色对应的菜单
     */
    @ManyToMany(targetEntity = Menu.class, fetch = FetchType.LAZY)
    @JoinTable(name = "AUTH_ROLE_MENU", joinColumns = @JoinColumn(name = "ROLE_CODE"), inverseJoinColumns = @JoinColumn(name = "MENU_ID"), foreignKey = @ForeignKey(name = "FK_ROLE_MENU"))
    private List<Menu> menus;
    /**
     * 角色对应的资源
     */
    @ManyToMany(targetEntity = Permission.class, fetch = FetchType.LAZY)
    @JoinTable(name = "AUTH_ROLE_PERMISSION", joinColumns = @JoinColumn(name = "ROLE_CODE"), inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID"))
    private List<Permission> permissions;
    /**
     * 对应的用户
     */
    @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinTable(name = "AUTH_ROLE_USER", joinColumns = @JoinColumn(name = "ROLE_CODE"), inverseJoinColumns = @JoinColumn(name = "USER_ID"), foreignKey = @ForeignKey(name = "FK_ROLE_USER_RCODE"))
    private List<User> users;
    /**
     * 角色对于的岗位
     */
    @ManyToMany(targetEntity = Job.class, fetch = FetchType.LAZY)
    @JoinTable(name = "AUTH_ROLE_JOB", joinColumns = @JoinColumn(name = "ROLE_CODE"), inverseJoinColumns = @JoinColumn(name = "JOB_ID"), foreignKey = @ForeignKey(name = "FK_ROLE_JOB_RID"))
    private List<Job> jobs;

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public String getId() {
        return this.id;
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    @JsonIgnore
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public String getAuthority() {
        return "ROLE_" + getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Role) {
            return this.id.equals(((Role) obj).getId());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return StringUtil.isNotBlank(id) ? id.hashCode() : super.hashCode();
    }

}