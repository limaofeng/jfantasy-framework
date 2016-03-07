package org.jfantasy.contacts.bean;

import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.security.bean.enums.Sex;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;

/**
 * 联系人表
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-3-15 上午11:11:59
 */
@Entity
@Table(name = "CONTACTS_LINKMAN")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "book", "groupIds"})
public class Linkman extends BaseBusEntity {

    private static final long serialVersionUID = 6682544433375302625L;

    @Id
    @Column(name = "ID", nullable = false, insertable = true, updatable = false, precision = 22, scale = 0)
    @GeneratedValue(generator = "fantasy-sequence")
    @GenericGenerator(name = "fantasy-sequence", strategy = "fantasy-sequence")
    private Long id;
    /**
     * 所属通讯录
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {javax.persistence.CascadeType.REFRESH})
    @JoinColumn(name = "BOOK_ID")
    private Book book;
    /**
     * 联系人照片
     */
    @Column(name = "AVATAR", length = 200)
    private String avatar;

    /**
     * 姓名
     */
    @Column(name = "NAME", length = 20)
    private String name;
    /**
     * 性别
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "SEX", length = 20)
    private Sex sex;
    /**
     * 公司
     */
    @Column(name = "COMPANY", length = 200)
    private String company;
    /**
     * 部门
     */
    @Column(name = "DEPARTMENT", length = 200)
    private String department;
    /**
     * 职位
     */
    @Column(name = "JOB", length = 200)
    private String job;

    /**
     * 移动电话
     */
    @Column(name = "MOBILE", length = 20)
    private String mobile;
    /**
     * E-mail
     */
    @Column(name = "EMAIL", length = 50)
    private String email;
    /**
     * 网址
     */
    @Column(name = "WEBSITE", length = 50)
    private String website;
    /**
     * 备注
     */
    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    /**
     * 所属分组<br/>
     * 多个分组以;分割
     */
    @ManyToMany(targetEntity = Group.class, fetch = FetchType.LAZY)
    @JoinTable(name = "CONTACTS_GROUP_LINKMAN", joinColumns = @JoinColumn(name = "LINKMAN_ID"), inverseJoinColumns = @JoinColumn(name = "GROUP_ID"))
    private List<Group> groups;

    @Transient
    private String groupNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @JsonSerialize(using = LinkmanGroupsSerialize.class)
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getGroupNames() {
        if (StringUtil.isNotBlank(groupNames)) {
            return groupNames;
        }
        if (ObjectUtil.isNotNull(this.getGroups())) {
            return groupNames = ObjectUtil.toString(this.getGroups(), "name", ";");
        }
        return groupNames;
    }

    public Long[] getGroupIds() {
        if (ObjectUtil.isNull(this.getGroups())) {
            return new Long[0];
        }
        return ObjectUtil.toFieldArray(this.groups, "id", Long.class);
    }

    public static class LinkmanGroupsSerialize extends JsonSerializer<List<Group>> {

        @Override
        public void serialize(List<Group> groups, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(ObjectUtil.toString(groups, "id", ";"));
        }

    }
}
