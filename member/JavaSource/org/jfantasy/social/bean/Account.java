package org.jfantasy.social.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.filestore.Image;
import org.jfantasy.filestore.converter.ImageConverter;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;
import org.jfantasy.security.bean.enums.Sex;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 社交账户
 */
@Entity
@Table(name = "SOCIAL_ACCOUNT")
@TableGenerator(name = "socialaccount_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "social_account:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Account {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, precision = 22)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "socialaccount_gen")
    private Long id;
    /**
     * 社交媒体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOCIALMEDIA_ID",foreignKey = @ForeignKey(name = "FK_SOCIALACCOUNT_SOCIALMEDIA"))
    private Socialmedia socialmedia;
    /**
     * 对应社交媒体的OPENID
     */
    @Column(name = "OPEN_ID",length = 50)
    private String openId;
    /**
     * 用户昵称
     */
    @Column(name = "NAME",length = 50)
    private String name;
    /**
     * 性别
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "SEX", length = 20)
    private Sex sex;
    /**
     * 头像
     */
    @Column(name = "AVATAR", length = 500)
    @Convert(converter = ImageConverter.class)
    private Image avatar;


    /**
     * 扩展属性
     */
    @Convert(converter = MapConverter.class)
    @Column(name = "PROPERTIES", columnDefinition = "Text")
    private Map<String, Object> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Socialmedia getSocialmedia() {
        return socialmedia;
    }

    public void setSocialmedia(Socialmedia socialmedia) {
        this.socialmedia = socialmedia;
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

}
