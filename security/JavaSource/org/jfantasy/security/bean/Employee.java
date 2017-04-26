package org.jfantasy.security.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.jfantasy.filestore.Image;
import org.jfantasy.filestore.converter.ImageConverter;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.security.Profile;
import org.jfantasy.security.bean.enums.EmployeeStatus;
import org.jfantasy.security.bean.enums.Sex;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 员工
 */
@Entity
@Table(name = "AUTH_EMPLOYEE")
@TableGenerator(name = "employee_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "auth_employee:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "user"})
public class Employee extends BaseBusEntity implements Profile {

    @Id
    @Column(name = "USER_ID", nullable = false, updatable = false, precision = 22)
    @GenericGenerator(name = "pkGenerator", strategy = "foreign", parameters = {@Parameter(name = "property", value = "user")})
    @GeneratedValue(generator = "pkGenerator")
    private Long id;
    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, precision = 20)
    private EmployeeStatus status;
    /**
     * 编号
     */
    @Column(name = "SN", nullable = false, precision = 20)
    private String sn;
    /**
     * 用户头像存储
     */
    @Column(name = "AVATAR", length = 500)
    @Convert(converter = ImageConverter.class)
    private Image avatar;
    /**
     * 名称
     */
    @NotNull(groups = {RESTful.POST.class, RESTful.PUT.class})
    @Column(name = "NAME", length = 30)
    private String name;
    /**
     * 生日
     */
    @NotNull(groups = {RESTful.POST.class, RESTful.PUT.class})
    @Column(name = "BIRTHDAY")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;
    /**
     * 性别
     */
    @NotNull(groups = {RESTful.POST.class, RESTful.PUT.class})
    @Enumerated(EnumType.STRING)
    @Column(name = "SEX", length = 10)
    private Sex sex;
    /**
     * 移动电话
     */
    @Column(name = "MOBILE", length = 20)
    private String mobile;
    /**
     * 固定电话
     */
    @Column(name = "TEL", length = 20)
    private String tel;
    /**
     * E-mail
     */
    @Column(name = "EMAIL", length = 50)
    private String email;
    /**
     * 部门
     */
    @JsonUnwrapped(prefix = "organization_")
    @JsonIgnoreProperties({"layer", "path", "description", "sort", "parent_id", "creator", "modifier", "create_time", "modify_time"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_EMPLOYEE_ORGANIZATION"))
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Organization organization;
    /**
     * 岗位
     */
    @JsonUnwrapped(prefix = "job_")
    @JsonIgnoreProperties({"creator", "modifier", "create_time", "modify_time"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_EMPLOYEE_JOB"))
    private Job job;
    /**
     * 员工档案
     */
    @Column(name = "PERSON_ID", precision = 22)
    private Long personId;

    @OneToOne(fetch = FetchType.LAZY, targetEntity = User.class, mappedBy = "employee")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        if(job.getId() == null){
            return;
        }
        this.job = job;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        if(organization.getId() == null){
            return;
        }
        this.organization = organization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    @Transient
    public List<Role> getRoles() {
        if(this.getJob() == null){
            return Collections.emptyList();
        }
        return this.getJob().getRoles();
    }

}
