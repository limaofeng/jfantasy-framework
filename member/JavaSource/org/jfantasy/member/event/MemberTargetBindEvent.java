package org.jfantasy.member.event;

import org.springframework.context.ApplicationEvent;


public class MemberTargetBindEvent extends ApplicationEvent {

    public MemberTargetBindEvent(MemberTargetBindSource source) {
        super(source);
    }

    public MemberTargetBindEvent(String type, Long memberId, String target) {
        super(new MemberTargetBindSource(type, memberId, target));
    }

    public MemberTargetBindSource getMemberTarget() {
        return (MemberTargetBindSource) this.getSource();
    }

}
