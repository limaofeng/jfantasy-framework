package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.spring.validation.Use;
import org.jfantasy.member.validators.TeamIdCannotRepeatValidator;

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
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "team_members", "member"})
public class Team extends BaseBusEntity {

    private static final long serialVersionUID = 4465203760129454882L;

    /**
     * 团队
     */
    @NotNull(groups = RESTful.POST.class)
    @Use(vali = TeamIdCannotRepeatValidator.class, groups = {RESTful.POST.class})
    @Id
    @Column(name = "CODE", nullable = false)
    private String key;
    /**
     * 团队类型
     */
    @NotNull(groups = RESTful.POST.class)
    @Column(name = "type", nullable = false)
    private String type;
    /**
     * 名称
     */
    @NotNull(groups = RESTful.POST.class)
    @Column(name = "NAME", nullable = false, length = 20)
    private String name;
    /**
     * 所有者账号
     */
    @Deprecated
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", updatable = false, foreignKey = @ForeignKey(name = "FK_TEAM_MEMBER"))
    private Member member;
    /**
     * 集团所有者
     */
    @NotNull(groups = RESTful.POST.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TEAM_TEAMMEMBER"))
    private TeamMember owner;
    /**
     * 描述
     */
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;
    /**
     * 扩展属性
     */
    @Convert(converter = MapConverter.class)
    @Column(name = "PROPERTIES", columnDefinition = "Text")
    private Map<String, Object> attributes;
    /**
     * 目标Id
     */
    @JsonProperty("target_id")
    @Column(name = "TARGET_ID", updatable = false, length = 32)
    private String targetId;
    /**
     * 目标类型
     */
    @JsonProperty("target_type")
    @Column(name = "TARGET_TYPE", updatable = false, length = 10)
    private String targetType;
    /**
     * 团队成员
     */
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<TeamMember> teamMembers;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Team() {
        super();
    }

    public Team(String id) {
        this();
        this.setKey(id);
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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public List<TeamMember> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }

    @Transient
    public void setMemberId(Long id) {
        this.member = new Member(id);
    }

    public TeamMember getOwner() {
        return owner;
    }

    public void setOwner(TeamMember owner) {
        this.owner = owner;
    }

    @Transient
    public Long getMemberId() {
        if (this.member == null) {
            return null;
        }
        return this.member.getId();
    }

}
