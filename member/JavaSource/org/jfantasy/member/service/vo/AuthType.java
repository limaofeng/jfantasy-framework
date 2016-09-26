package org.jfantasy.member.service.vo;

public enum AuthType {
    /**
     * 通过Token授权
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
