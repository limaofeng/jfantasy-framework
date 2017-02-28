package org.jfantasy.member.bean;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

public class MemberTargetKey implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER", nullable = false, updatable = false)
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE", nullable = false, updatable = false)
    private MemberType type;

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

    public static MemberTargetKey newInstance(Member member, MemberType type) {
        MemberTargetKey targetKey = new MemberTargetKey();
        targetKey.setMember(member);
        targetKey.setType(type);
        return targetKey;
    }

}
