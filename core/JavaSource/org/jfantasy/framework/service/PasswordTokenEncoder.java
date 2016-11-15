package org.jfantasy.framework.service;

/**
 * 短信验证码接口
 */
public interface PasswordTokenEncoder {

    /**
     * @param operation       操作
     * @param type            token 类型
     * @param username        用户名称
     * @param rawPassword     原密码
     * @param encodedPassword 用户输入密码
     * @return boolean
     */
    boolean matches(String operation, PasswordTokenType type, String username, String rawPassword, String encodedPassword);

}
