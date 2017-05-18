package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.member.bean.enums.EnterpriseStatus;

import javax.persistence.*;

/**
 * 企业信息
 */
@Entity
@Table(name = "MEM_TEAM_ENTERPRISE")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "team"})
public class Enterprise extends BaseBusEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, precision = 32)
    @GenericGenerator(name = "pkGenerator", strategy = "foreign", parameters = {@org.hibernate.annotations.Parameter(name = "property", value = "team")})
    @GeneratedValue(generator = "pkGenerator")
    private String id;
    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30)
    private EnterpriseStatus status;

    /**
     * 公司名称
     */
    @Column(name = "NAME", length = 50)
    private String name;
    /**
     * 地址
     */
    @Column(name = "ADDRESS", length = 150)
    private String address;
    /**
     * 电话
     */
    @Column(name = "TEL", length = 12)
    private String tel;
    /**
     * 邮箱
     */
    @Column(name = "EMAIL", length = 30)
    private String email;
    /**
     * 联系人名称
     */
    @Column(name = "CONTACT", length = 50)
    private String contact;
    /**
     * 联系人电话
     */
    @Column(name = "CONTACT_TEL", length = 12)
    private String contactTel;
    /**
     * 对于的团队
     */
    @OneToOne(fetch = FetchType.LAZY, targetEntity = Team.class, mappedBy = "enterprise")
    private Team team;
    /**
     * 目标Id
     */
    @Column(name = "TARGET_ID", length = 32)
    private String target;
    /**
     * 集团规模
     */
    @Column(name = "SCALE", length = 32)
    private String scale;
    /**
     * 所属行业
     */
    @Column(name = "INDUSTRY", length = 32)
    private String industry;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public EnterpriseStatus getStatus() {
        return status;
    }

    public void setStatus(EnterpriseStatus status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
