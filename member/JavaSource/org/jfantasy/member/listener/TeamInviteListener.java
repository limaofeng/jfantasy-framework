package org.jfantasy.member.listener;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.event.TeamInviteEvent;
import org.jfantasy.member.service.MemberService;
import org.jfantasy.member.service.TeamMemberService;
import org.jfantasy.ms.EventEmitter;
import org.jfantasy.ms.LinkerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 团队要求监听
 */
@Component
public class TeamInviteListener implements ApplicationListener<TeamInviteEvent> {

    private TeamMemberService teamMemberService;
    private MemberService memberService;
    private EventEmitter eventEmitter;

    @Override
    @Async
    @Transactional
    public void onApplicationEvent(TeamInviteEvent event) {
        Member member = memberService.findUniqueByUsername(event.getMobile());
        TeamMember teamMember = teamMemberService.findUnique(event.getTeamId(), event.getMobile());
        if (member != null && teamMember != null) {
            teamMemberService.activate(teamMember.getId(),member.getId());
        }

        if (teamMember != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("teamMemberId",teamMember.getId());
            data.put("status",teamMember.getStatus());
            data.put("teamName",teamMember.getTeam().getName());
            data.put("teamId",teamMember.getTeam().getKey());
            eventEmitter.fireEvent("tmember.new",teamMember.getId()+"",String.format("[%s]新增集团员工",teamMember.getId()), JSON.serialize(data),
                new LinkerBean("teammember",teamMember.getName(),teamMember.getId()+""));
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
    @Autowired
    public void setEventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

}
