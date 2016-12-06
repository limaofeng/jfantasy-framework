package org.jfantasy.trade.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.trade.bean.enums.ProjectType;

import javax.persistence.*;

/**
 * 交易项目
 */
@Entity
@Table(name = "PAY_PROJECT")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Project extends BaseBusEntity {

    public static final String PAYMENT = "payment";
    public static final String REFUND = "refund";
    public static final String INCOME = "income";
    public static final String INPOUR = "inpour";
    public static final String WITHDRAWAL = "withdrawal";
    public static final String TRANSFER = "transfer";

    private static final long serialVersionUID = -2461534613032864306L;

    /**
     * 编码
     */
    @Id
    @Column(name = "CODE", updatable = false)
    private String key;
    /**
     * 项目类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private ProjectType type;
    /**
     * 名称
     */
    @Column(name = "NAME")
    private String name;
    /**
     * 描述
     */
    @Column(name = "DESCRIPTION")
    private String description;

    public Project(String key) {
        this.key = key;
    }

    public Project() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

}
