package org.jfantasy.sns.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.sns.bean.enums.PlatformType;

import javax.persistence.*;

@Entity
@Table(name = "SNS_PLATFORM")
@TableGenerator(name = "platform_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "sns_platform:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Platform extends BaseBusEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, precision = 22)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "platform_gen")
    private Long id;
    /**
     * 类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 20)
    private PlatformType type;
    /**
     * 第三方平台提供的应用ID
     */
    @Column(name = "appId", length = 32)
    private String appId;
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

    public PlatformType getType() {
        return type;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public void setType(PlatformType type) {
        this.type = type;
    }

}
