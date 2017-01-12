package org.jfantasy.sns.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.filestore.Image;
import org.jfantasy.filestore.converter.ImageConverter;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;
import org.jfantasy.member.bean.Member;
import org.jfantasy.security.bean.enums.Sex;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 社交账户
 */
@Entity
@Table(name = "SNS_ACCOUNT")
@TableGenerator(name = "snser_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "sns_account:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "member"})
public class Snser {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, precision = 22)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "snser_gen")
    private Long id;
    /**
     * 社交媒体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLATFORM_ID", foreignKey = @ForeignKey(name = "FK_ACCOUNT_PLATFORM"))
    private Platform platform;
    /**
     * 对应社交媒体的OPENID
     */
    @Column(name = "OPEN_ID", length = 50)
    private String openId;
    /**
     * 头像
     */
    @Column(name = "AVATAR", length = 500)
    @Convert(converter = ImageConverter.class)
    private Image avatar;
    /**
     * 用户昵称
     */
    @Column(name = "NAME", length = 50)
    private String name;
    /**
     * 性别
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "SEX", length = 20)
    private Sex sex;
    /**
     * 扩展属性
     */
    @Convert(converter = MapConverter.class)
    @Column(name = "PROPERTIES", columnDefinition = "Text")
    private Map<String, Object> properties;
    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", foreignKey = @ForeignKey(name = "FK_ACCOUNT_MEMBER"))
    private Member member;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        if (value == null) {
            return;
        }
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

}
