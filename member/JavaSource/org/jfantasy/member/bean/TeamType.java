package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MEM_TEAM_TYPE")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class TeamType extends BaseBusEntity {
    /**
     * 类型标识
     */
    @Id
    @Column(name = "ID", length = 10)
    private String id;
    /**
     * 名称
     */
    @Column(name = "NAME", length = 50)
    private String name;
    /**
     * 所有者默认角色
     */
    @Column(name = "OWNER_ROLE", length = 32)
    private String ownerRole;

    public TeamType(){
        super();
    }

    public TeamType(String id) {
        super();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerRole() {
        return ownerRole;
    }

    public void setOwnerRole(String ownerRole) {
        this.ownerRole = ownerRole;
    }
}
