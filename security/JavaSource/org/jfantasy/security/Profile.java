package org.jfantasy.security;

import org.jfantasy.security.bean.enums.Sex;

import java.util.Date;

public interface Profile {
    /**
     * 姓名
     */
    String getName();

    /**
     * 性别
     */
    Sex getSex();

    /**
     * 生日
     */
    Date getBirthday();

    /**
     * 移动电话
     */
    String getMobile();

    /**
     * 固定电话
     */
    String getTel();

    /**
     * E-mail
     */
    String getEmail();

    /**
     * 描述信息
     */
    String getDescription();

    Long getId();

}
