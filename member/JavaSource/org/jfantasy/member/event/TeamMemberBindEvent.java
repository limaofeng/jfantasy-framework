package org.jfantasy.member.event;


import org.springframework.context.ApplicationEvent;

/**
 * 集团员工绑定本人档案
 */
public class TeamMemberBindEvent extends ApplicationEvent{
    public TeamMemberBindEvent(Long teamMemberId){
        super(teamMemberId);
    }
}
