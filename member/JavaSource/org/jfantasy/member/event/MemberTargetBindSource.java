package org.jfantasy.member.event;


import java.io.Serializable;

public class MemberTargetBindSource implements Serializable {

    private String type;
    private Long memberId;
    private String target;

    public MemberTargetBindSource(String type, Long memberId, String target) {
        this.type = type;
        this.memberId = memberId;
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getTarget() {
        return target;
    }
}
