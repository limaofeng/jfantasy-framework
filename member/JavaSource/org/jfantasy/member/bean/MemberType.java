package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "MEM_MEMBER_TYPE")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "members"})
public class MemberType extends BaseBusEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, length = 20)
    private String id;
    /**
     * 名称
     */
    @Column(name = "NAME", nullable = false, length = 50)
    private String name;
    /**
     * profileUrl
     */
    @Column(name = "PROFILE_URL", nullable = false, length = 50)
    private String profileUrl;
    /**
     * 对于的用户
     */
    @ManyToMany(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinTable(name = "MEM_MEMBER_TARGET", joinColumns = @JoinColumn(name = "TYPE"), inverseJoinColumns = @JoinColumn(name = "MEMBER"), foreignKey = @ForeignKey(name = "FK_MEMBERTARGET_TID"))
    private List<Member> members;

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

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
