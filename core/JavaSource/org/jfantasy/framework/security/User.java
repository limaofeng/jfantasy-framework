package org.jfantasy.framework.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-08 17:06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Principal {
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 名称
     */
    private String name;
    /**
     * 称号
     */
    private String title;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 签名
     */
    private String signature;
    /**
     * 组名
     */
    private String group;
    /**
     * 电话
     */
    private String phone;

    /**
     * 权限
     */
    private List<String> authoritys;
}