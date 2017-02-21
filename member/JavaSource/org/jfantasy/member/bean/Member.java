package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.converter.StringsConverter;
import org.jfantasy.framework.spring.validation.RESTful.POST;
import org.jfantasy.framework.spring.validation.RESTful.PUT;
import org.jfantasy.framework.spring.validation.Use;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.member.validators.UsernameCannotRepeatValidator;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会员
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-22 上午11:25:14
 */
@Entity
@Table(name = "MEM_MEMBER", uniqueConstraints = {
        @UniqueConstraint(name = "UK_MEMBER_TARGET", columnNames = {"TARGET_TYPE", "TARGET_ID"})
})
@JsonPropertyOrder({"id", "type", "username", "nick_name", "enabled", "non_locked", "non_expired", "credentials_non_expired", "lock_time", "last_login_time", "code"})
@TableGenerator(name = "member_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "mem_member:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "user_groups", "roles", "authorities"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Member extends BaseBusEntity {

    public static final String MEMBER_TYPE_PERSONAL = "personal";

    private static final long serialVersionUID = -4479116155241989100L;

    @Null(groups = {POST.class})
    @Id
    @Column(name = "ID", nullable = false, updatable = false, precision = 22)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "member_gen")
    private Long id;
    /**
     * 用户类型
     */
    @ManyToMany(targetEntity = MemberType.class, fetch = FetchType.LAZY)
    @JoinTable(name = "MEM_MEMBER_TARGET", joinColumns = @JoinColumn(name = "MEMBER"), inverseJoinColumns = @JoinColumn(name = "TYPE"), foreignKey = @ForeignKey(name = "FK_MEMBERTARGET_MID"))
    private List<MemberType> types;
    /**
     * 用户登录名称
     */
    @NotEmpty(groups = {POST.class, PUT.class})
    @Length(min = 8, max = 20, groups = {POST.class, PUT.class})
    @Use(vali = UsernameCannotRepeatValidator.class, groups = {POST.class})
    @Column(name = "USERNAME", length = 20, updatable = false, nullable = false, unique = true)
    private String username;
    /**
     * 登录密码
     */
    @NotEmpty(groups = {POST.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "PASSWORD", length = 20, nullable = false)
    private String password;
    /**
     * 用户显示昵称
     */
    @Column(name = "NICK_NAME", length = 50)
    private String nickName;
    /**
     * 是否启用
     */
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;
    /**
     * 未过期
     */
    @Column(name = "NON_EXPIRED", nullable = false)
    private Boolean accountNonExpired;
    /**
     * 未锁定
     */
    @Column(name = "NON_LOCKED", nullable = false)
    private Boolean accountNonLocked;
    /**
     * 未失效
     */
    @Column(name = "CREDENTIALS_NON_EXPIRED", nullable = false)
    private Boolean credentialsNonExpired;
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
     * 会员其他信息
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @PrimaryKeyJoinColumn
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private MemberDetails details;
    /**
     * 目标Id
     */
    @Column(name = "TARGET_ID", updatable = false, length = 32)
    @JsonProperty("target_id")
    private String targetId;
    /**
     * 目标类型
     */
    @Column(name = "TARGET_TYPE", updatable = false, length = 10)
    @JsonProperty("target_type")
    private String targetType;
    /**
     * 用户标签
     */
    @Column(name = "TAGS", length = 300)
    @Convert(converter = StringsConverter.class)
    private String[] tags;
    @Transient
    private String code;
    @Transient
    private String type;
    @Transient
    private String[] authorities;

    public Member() {//NOSONAR
    }//NOSONAR

    public Member(Long id) {//NOSONAR
        this.id = id;//NOSONAR
    }//NOSONAR

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public MemberDetails getDetails() {
        return details;
    }

    public void setDetails(MemberDetails details) {
        this.details = details;
        if (details != null) {
            this.details.setMember(this);
        }
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<MemberType> getTypes() {
        return types;
    }

    public void setTypes(List<MemberType> types) {
        this.types = types;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void addType(MemberType type) {
        if (this.types == null) {
            this.types = new ArrayList<>();
        }
        this.types.add(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", type='" + ObjectUtil.toString(this.types, "id", ",") + '\'' +
                ", username='" + username + '\'' +
                ", nickName='" + nickName + '\'' +
                ", enabled=" + enabled +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", lockTime=" + lockTime +
                ", lastLoginTime=" + lastLoginTime +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

}
