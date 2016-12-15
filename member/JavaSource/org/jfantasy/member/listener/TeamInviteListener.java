package org.jfantasy.member.listener;

import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.event.TeamInviteEvent;
import org.jfantasy.member.service.MemberService;
import org.jfantasy.member.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 团队要求监听
 */
@Component
public class TeamInviteListener implements ApplicationListener<TeamInviteEvent> {

    private TeamMemberService teamMemberService;
    private MemberService memberService;

    @Override
    @Async
    @Transactional
    public void onApplicationEvent(TeamInviteEvent event) {
        Member member = memberService.findUniqueByUsername(event.getMobile());
        TeamMember teamMember = teamMemberService.findUnique(event.getTeamId(), event.getMobile());
        if (member != null && teamMember != null) {
            teamMemberService.activate(teamMember.getId(),member.getId());
        }
    }

    @Autowired
    public void setTeamMemberService(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @Autowired
    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

}
