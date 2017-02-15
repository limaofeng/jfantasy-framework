package org.jfantasy.security.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.spring.validation.Use;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.security.bean.enums.UserType;
import org.jfantasy.security.validators.UsernameCannotRepeatValidator;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "AUTH_USER")
@TableGenerator(name = "user_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "auth_user:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "roles", "userGroups", "website", "menus", "authorities", "details"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends BaseBusEntity {

    private static final long serialVersionUID = 5507435998232223911L;

    @Transient
    private String code;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    @Id
    @Column(name = "ID", nullable = false, updatable = false, precision = 22)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_gen")
    private Long id;

    /**
     * 用户登录名称
     */
    @NotEmpty(groups = {RESTful.POST.class, RESTful.PUT.class})
    @Length(min = 8, max = 20, groups = {RESTful.POST.class, RESTful.PUT.class})
    @Use(vali = UsernameCannotRepeatValidator.class, groups = {RESTful.POST.class})
    @Column(name = "USERNAME", length = 20, updatable = false, nullable = false, unique = true)
    private String username;
    /**
     * 登录密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "PASSWORD", length = 20, nullable = false)
    private String password;

    /**
     * 用户类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "USER_TYPE", length = 20, nullable = false)
    private UserType userType;
    /**
     * 用户显示昵称
     */
    @Column(name = "NICK_NAME", length = 50)
    private String nickName;
    /**
     * 是否启用
     */
    @Column(name = "ENABLED")
    private boolean enabled;
    /**
     * 未过期
     */
    @Column(name = "NON_EXPIRED")
    private boolean accountNonExpired;
    /**
     * 未锁定
     */
    @Column(name = "NON_LOCKED")
    private boolean accountNonLocked;
    /**
     * 未失效
     */
    @Column(name = "CREDENTIALS_NON_EXPIRED")
    private boolean credentialsNonExpired;
    /**
     * 锁定时间
     */
    @Column(name = "LOCK_TIME")
    private Date lockTime;
    /**
     * 最后登录时间
     */
    @Column(name = "LAST_LOGIN_TIME")
    private Date lastLoginTime;
    /**
     * 用户对应的用户组
     */
    @ManyToMany(targetEntity = UserGroup.class, fetch = FetchType.LAZY)
    @JoinTable(name = "AUTH_USERGROUP_USER", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "USERGROUP_ID"), foreignKey = @ForeignKey(name = "FK_USERGROUP_USER_US"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<UserGroup> userGroups;
    /**
     * 用户对应的角色
     */
    @ManyToMany(targetEntity = Role.class, fetch = FetchType.LAZY)
    @JoinTable(name = "AUTH_ROLE_USER", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_CODE"), foreignKey = @ForeignKey(name = "FK_ROLE_USER_UID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Role> roles;
    /**
     * 用户详细信息
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @PrimaryKeyJoinColumn
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserDetails details;
    /**
     * 用户关联的员工信息
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @PrimaryKeyJoinColumn
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Employee employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public List<UserGroup> getUserGroups() {
        return this.userGroups;
    }

    public Date getLockTime() {
        return this.lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Date getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.employee.setUser(this);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setDetails(UserDetails details) {
        this.details = details;
        this.details.setUser(this);
    }

    public UserDetails getDetails() {
        return details;
    }

    @Transient
    public String[] getAuthorities() {
        Set<String> authorities = new LinkedHashSet<>();
        for (UserGroup userGroup : this.getUserGroups()) {
            if (!userGroup.isEnabled()) {
                continue;
            }
            authorities.add(userGroup.getAuthority());
            authorities.addAll(Arrays.asList(userGroup.getRoleAuthorities()));
        }
        // 添加角色权限
        for (Role role : this.getRoles()) {
            authorities.add(role.getAuthority());
        }
        return authorities.toArray(new String[authorities.size()]);
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @JsonIgnore
    @Transient
    public Set<Role> getAllRoles(){
        Set<Role> allroles = new HashSet<>();
        allroles.addAll(ObjectUtil.filter(ObjectUtil.defaultValue(this.getRoles(),new ArrayList<>()), Role::isEnabled));
        if (this.getUserType() != UserType.employee) {
            return allroles;
        }
        allroles.addAll(ObjectUtil.filter(this.getEmployee().getRoles(), Role::isEnabled));
        return allroles;
    }

    @JsonIgnore
    @Transient
    public Set<Menu> getAllMenus() {
        Set<Menu> menus = new HashSet<>();
        for (Role role : this.getAllRoles()) {
            menus.addAll(role.getMenus());
        }
        for (UserGroup group : this.getUserGroups()) {
            menus.addAll(group.getMenus());
        }
        if (this.getUserType() != UserType.employee) {
            return menus;
        }
        return menus;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickName='" + nickName + '\'' +
                ", enabled=" + enabled +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", lockTime=" + lockTime +
                ", lastLoginTime=" + lastLoginTime +
                ", details=" + details +
                '}';
    }

}