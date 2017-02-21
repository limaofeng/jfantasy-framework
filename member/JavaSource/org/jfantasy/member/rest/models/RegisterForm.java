package org.jfantasy.member.rest.models;


import org.jfantasy.framework.spring.validation.RESTful;

import javax.validation.constraints.NotNull;

public class RegisterForm {
    /**
     * 用户名
     */
    @NotNull(groups = RESTful.POST.class)
    private String username;
    /**
     * 密码
     */
    @NotNull(groups = RESTful.POST.class)
    private String password;
    /**
     * 注册短信码
     */
    private String macode;

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

    public String getMacode() {
        return macode;
    }

    public void setMacode(String macode) {
        this.macode = macode;
    }
}
