package org.jfantasy.auth.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.sns.bean.enums.PlatformType;

import javax.validation.constraints.NotNull;

public class SnsLoginForm {
    /**
     * 用户类型
     */
    @JsonProperty("user_type")
    private String userType;
    /**
     * 平台类型
     */
    private PlatformType type;
    /**
     * appid
     */
    private String appId;
    /**
     * 授权码
     */
    @NotNull(groups = RESTful.POST.class)
    private String code;
    /**
     * 范围
     */
    private Scope scope = Scope.member;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public PlatformType getType() {
        return type;
    }

    public void setType(PlatformType type) {
        this.type = type;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

}
