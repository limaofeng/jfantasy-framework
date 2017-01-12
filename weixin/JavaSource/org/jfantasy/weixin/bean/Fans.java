package org.jfantasy.weixin.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.weixin.bean.enums.Sex;

import javax.persistence.*;

/**
 * 微信用户基本信息
 * Created by 李茂峰 on 2017/01/12.
 */
@Entity
@IdClass(UserKey.class)
@Table(name = "WEIXIN_USER")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Fans extends BaseBusEntity {

    @Id
    private String appId;
    //用户的标识，对当前公众号唯一
    @Id
    private String openId;
    //用户的昵称
    @Column(name = "NICKNAME", length = 50)
    private String nickname;
    //用户的性别
    @Enumerated(EnumType.STRING)
    @Column(name = "SEX", length = 10)
    private Sex sex;
    //用户所在城市
    @Column(name = "CITY", length = 50)
    private String city;
    //用户所在国家
    @Column(name = "COUNTRY", length = 50)
    private String country;
    //用户所在省份
    @Column(name = "PROVINCE", length = 50)
    private String province;
    //用户的语言，简体中文为zh_CN
    @Column(name = "LANGUAGE", length = 20)
    private String language;
    //用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
    @Column(name = "AVATAR", length = 500)
    private String avatar;
    //用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
    @Column(name = "SUBSCRIBE_TIME")
    private Long subscribeTime;
    //用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
    @Column(name = "SUBSCRIBE")
    private Boolean subscribe;
    @Column(name = "UNION_ID", length = 100)
    private String unionId;
    //最后消息时间
    @Column(name = "LAST_MESSAGE_TIME")
    private Long lastMessageTime;
    //最后查看消息时间
    @Column(name = "LAST_LOOK_TIME")
    private Long lastLookTime;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Long subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public Long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Long getLastLookTime() {
        return lastLookTime;
    }

    public void setLastLookTime(Long lastLookTime) {
        this.lastLookTime = lastLookTime;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

}
