package org.jfantasy.member;

public interface ProfileService {

    /**
     * 通过 profile id 查询对象
     *
     * @param id value
     * @return Profile
     */
    Profile loadProfile(String id);

    /**
     * 查询 Profile 对象
     *
     * @param name 手机号
     * @return Profile
     */
    Profile loadProfileByPhone(String name);

}
