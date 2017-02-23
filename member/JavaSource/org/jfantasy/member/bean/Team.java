package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.spring.validation.Use;
import org.jfantasy.member.bean.databind.TeamTypeDeserializer;
import org.jfantasy.member.bean.databind.TeamTypeSerializer;
import org.jfantasy.member.bean.enums.TeamStatus;
import org.jfantasy.member.validators.TeamIdCannotRepeatValidator;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.bean.User;
import org.jfantasy.security.bean.databind.UserDeserializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队/小组
 */
@Entity
@Table(name = "MEM_TEAM")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "team_members", "member", "enterprise"})
public class Team extends BaseBusEntity {

    private static final long serialVersionUID = 4465203760129454882L;

    /**
     * 团队
     */
    @NotNull(groups = RESTful.POST.class)
    @Use(vali = TeamIdCannotRepeatValidator.class, groups = {RESTful.POST.class})
    @Id
    @Column(name = "CODE", nullable = false, length = 32)
    private String key;
    /**
     * 团队类型
     */
    @NotNull(groups = RESTful.POST.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonDeserialize(using = TeamTypeDeserializer.class)
    @JsonSerialize(using = TeamTypeSerializer.class)
    @JoinColumn(name = "TYPE", nullable = false, foreignKey = @ForeignKey(name = "FK_TEAM_TEAMTYPE"))
    private TeamType type;
    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 10)
    private TeamStatus status;
    /**
     * 名称
     */
    @NotNull(groups = RESTful.POST.class)
    @Column(name = "NAME", nullable = false, length = 20)
    private String name;
    /**
     * 集团所有者
     */
    @Column(name = "OWNER_ID", precision = 22)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long ownerId;
    /**
     * 描述
     */
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;
    /**
     * 团队成员
     */
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<TeamMember> teamMembers;
    /**
     * 负责人
     */
    @JsonDeserialize(using = UserDeserializer.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFICER", foreignKey = @ForeignKey(name = "FK_TEAM_USER"))
    private User officer;
    /**
     * 扩展属性
     */
    @Convert(converter = MapConverter.class)
    @Column(name = "PROPERTIES", columnDefinition = "Text")
    private Map<String, Object> attributes;
    /**
     * 企业信息
     */
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Enterprise enterprise;

    @Transient
    private TeamMember owner;
    @Transient
    private String role;
    @Transient
    private String roleName;

    public TeamType getType() {
        return type;
    }

    public void setType(TeamType type) {
        this.type = type;
    }

    public TeamStatus getStatus() {
        return status;
    }

    public void setStatus(TeamStatus status) {
        this.status = status;
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

    @JsonAnySetter
    public void set(String key, Object value) {
        if (value == null) {
            return;
        }
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TeamMember> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public TeamMember getOwner() {
        return owner;
    }

    public void setOwner(TeamMember owner) {
        this.owner = owner;
    }

    public User getOfficer() {
        return officer;
    }

    public void setOfficer(User officer) {
        this.officer = officer;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public void setRole(Role role) {
        if (role == null) {
            return;
        }
        this.role = role.getId();
        this.roleName = role.getName();
    }

    public String getRole() {
        return role;
    }

    public String getRoleName() {
        return roleName;
    }

    public static Team newInstance(String id) {
        Team team = new Team();
        team.setKey(id);
        return team;
    }

    public interface Owner {
        public interface PUT {
        }
    }

}
