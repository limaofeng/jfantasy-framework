package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;

@Entity
@IdClass(MemberTargetKey.class)
@Table(name = "MEM_MEMBER_TARGET", uniqueConstraints = @UniqueConstraint(name = "UK_MEMBERTARGET", columnNames = {"MEMBER", "TYPE", "VALUE"}))
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class MemberTarget extends BaseBusEntity {

    @Id
    private Member member;
    @Id
    private MemberType type;
    /**
     * 关联业务ID
     */
    @Column(name = "VALUE", length = 32, nullable = false)
    private String value;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public MemberType getType() {
        return type;
    }

    public void setType(MemberType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static MemberTarget newInstance(Member member, MemberType type, String value) {
        MemberTarget target = new MemberTarget();
        target.setMember(member);
        target.setType(type);
        target.setValue(value);
        return target;
    }
}
