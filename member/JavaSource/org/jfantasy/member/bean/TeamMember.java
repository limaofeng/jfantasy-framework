package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.hibernate.converter.MapConverter;
import org.jfantasy.framework.dao.hibernate.converter.StringArrayConverter;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.spring.validation.UseLong;
import org.jfantasy.member.bean.enums.PapersType;
import org.jfantasy.member.bean.enums.TeamMemberStatus;
import org.jfantasy.member.rest.validators.ChangeTeamOwnerValidator;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.bean.databind.RoleDeserializer;
import org.jfantasy.security.bean.databind.RoleSerializer;
import org.jfantasy.security.bean.enums.Sex;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 团队成员
 */
@Entity
@Table(name = "MEM_TEAM_MEMBER", uniqueConstraints = @UniqueConstraint(name = "UK_TEAM_MEMBER", columnNames = {"MEMBER_ID", "TEAM_ID"}))
@GenericGenerator(name = "team_member_gen", strategy = "enhanced-table",
        parameters = {
                @Parameter(name = "table_name", value = "sys_sequence"),
                @Parameter(name = "value_column_name", value = "gen_value"),
                @Parameter(name = "segment_column_name", value = "gen_name"),
                @Parameter(name = "segment_value", value = "mem_team_member:id"),
                @Parameter(name = "increment_size", value = "10"),
                @Parameter(name = "optimizer", value = "pooled-lo")
        })
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "team", "member"})
public class TeamMember extends BaseBusEntity {

    private static final long serialVersionUID = -7880093458033934231L;

    @Id
    @Column(name = "ID", nullable = false, updatable = false, precision = 22)
    @GeneratedValue(generator = "team_member_gen")
    @UseLong(vali = ChangeTeamOwnerValidator.class, groups = {Team.Owner.PUT.class})
    protected Long id;
    /**
     * 用户名称
     */
    @NotNull(groups = RESTful.POST.class)
    @Column(name = "NAME", length = 50)
    private String name;
    /**
     * 性别
     */
    @Enumerated(EnumType.STRING)
    @NotNull(groups = RESTful.POST.class)
    @Column(name = "SEX", length = 20)
    private Sex sex;
    /**
     * 邮箱
     */
    @NotNull(groups = RESTful.POST.class)
    @Column(name = "EMAIL", length = 50)
    private String email;
    /**
     * 电话
     */
    @NotNull(groups = {RESTful.POST.class})
    @Column(name = "MOBILE", length = 20)
    private String mobile;
    /**
     * 证件类型
     */
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {RESTful.POST.class})
    @Column(name = "PAPERS_TYPE", length = 20)
    private PapersType papersType;
    /**
     * 证件号码
     */
    @NotNull(groups = {RESTful.POST.class})
    @Column(name = "PAPERS_NUMBER", length = 30)
    private String papersNumber;
    /**
     * 岗位
     */
    @Column(name = "POSITION", length = 20)
    private String position;
    /**
     * 部门
     */
    @Column(name = "DEPT", length = 20)
    private String dept;
    /**
     * 备注
     */
    @Column(name = "NOTES", length = 250)
    private String notes;
    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private TeamMemberStatus status;
    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", foreignKey = @ForeignKey(name = "FK_TEAMMEMBER_MEMBER"))
    private Member member;
    /**
     * 动态属性
     */
    @Convert(converter = MapConverter.class)
    @Column(name = "PROPERTIES", columnDefinition = "Text")
    private Map<String, String> properties;
    /**
     * 团队ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_TEAMMEMBER_TEAM"))
    private Team team;
    /**
     * 标签
     */
    @Convert(converter = StringArrayConverter.class)
    @Column(name = "TAGS", length = 2000)
    private String[] tags;
    /**
     * 关联角色
     */
    @JsonDeserialize(using = RoleDeserializer.class)
    @JsonSerialize(using = RoleSerializer.class)
    @NotNull(groups = RESTful.POST.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", foreignKey = @ForeignKey(name = "FK_TEAMMEMBER_ROLE"))
    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamMemberStatus getStatus() {
        return status;
    }

    public void setStatus(TeamMemberStatus status) {
        this.status = status;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @JsonAnySetter
    public void set(String key, String value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
    }

    @Transient
    public String get(String key) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(key);
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PapersType getPapersType() {
        return papersType;
    }

    public void setPapersType(PapersType papersType) {
        this.papersType = papersType;
    }

    public String getPapersNumber() {
        return papersNumber;
    }

    public void setPapersNumber(String papersNumber) {
        this.papersNumber = papersNumber;
    }

    @Transient
    @NotNull(groups = RESTful.POST.class)
    public String getTeamId() {
        return this.team.getKey();
    }

    public void setTeamId(String id) {
        this.team = new Team();
        this.team.setKey(id);
    }

    @Transient
    public Long getMemberId() {
        return this.member == null ? null : this.member.getId();
    }

    public void setMemberId(Long id) {
        this.member = new Member();
        this.member.setId(id);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Transient
    public String getIdCard() {
        return this.getPapersNumber();
    }

    @Transient
    public void setIdCard(String idCard) {
        this.setPapersType(PapersType.idcard);
        this.setPapersNumber(idCard);
    }

}
