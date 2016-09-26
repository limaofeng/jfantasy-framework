package org.jfantasy.member.service.vo;

public enum AuthType {
    /**
     * token 授权修改
     */
    token,
    /**
     * 密码登陆
     */
    password,
    /**
     * 短信验证码登陆
     */
    macode
}
