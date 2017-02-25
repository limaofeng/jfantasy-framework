package org.jfantasy.member;

public interface ProfileService {

    /**
     * 查询 Profile 对象
     *
     * @param name 手机号
     * @return Profile
     */
    Profile loadProfileByPhone(String name);

}
