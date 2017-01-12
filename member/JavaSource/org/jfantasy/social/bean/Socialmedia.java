package org.jfantasy.social.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.social.bean.enums.SocialmediaType;

import javax.persistence.*;

//Social account

@Entity
@Table(name = "SOCIAL_MEDIA")
@TableGenerator(name = "socialmedia_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "social_media:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Socialmedia extends BaseBusEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, precision = 22)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "socialmedia_gen")
    private Long id;
    /**
     * 类型
     */
    @Column(name = "TYPE", length = 20)
    private SocialmediaType type;
    /**
     * 第三方编码
     */
    @Column(name = "SN", length = 32)
    private String sn;
    /**
     * 名称
     */
    @Column(name = "NAME", length = 20)
    private String name;
    /**
     * 备注
     */
    @Column(name = "NOTES", length = 50)
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SocialmediaType getType() {
        return type;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setType(SocialmediaType type) {
        this.type = type;
    }

}
