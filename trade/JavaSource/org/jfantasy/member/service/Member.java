package org.jfantasy.member.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Member {


    private Long id;
    /**
     * 用户类型
     */
    private String type;
    /**
     * 用户登录名称
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 用户显示昵称
     */
    private String nickName;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 未过期
     */
    @JsonProperty("non_expired")
    private Boolean accountNonExpired;
    /**
     * 未锁定
     */
    @JsonProperty("non_locked")
    private Boolean accountNonLocked;
    /**
     * 未失效
     */
    @JsonProperty("credentials_non_expired")
    private Boolean credentialsNonExpired;
    /**
     * 锁定时间
     */
    @JsonProperty("lock_time")
    private Date lockTime;
    /**
     * 最后登录时间
     */
    @JsonProperty("last_login_time")
    private Date lastLoginTime;
    /**
     * 目标Id
     */
    @JsonProperty("target_id")
    private String targetId;
    /**
     * 目标类型
     */
    @JsonProperty("target_type")
    private String targetType;
    /**
     * 用户标签
     */
    private String[] tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

}
