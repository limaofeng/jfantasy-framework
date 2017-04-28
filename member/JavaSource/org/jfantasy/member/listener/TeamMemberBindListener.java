package org.jfantasy.member.listener;

import org.apache.commons.collections.map.HashedMap;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.event.TeamMemberBindEvent;
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
 * 集团员工绑定本人档案
 */
@Component
public class TeamMemberBindListener implements ApplicationListener<TeamMemberBindEvent>{
    private TeamMemberService teamMemberService;
    private EventEmitter eventEmitter;
    @Override
    @Async
    @Transactional
    public void onApplicationEvent(TeamMemberBindEvent event) {
        Long teamMemberId = (long) event.getSource();
        TeamMember teamMember = teamMemberService.get(teamMemberId);
        Map<String,Object> data = new HashMap<>();
        data.put("memberId",teamMember.getMemberId());
        data.put("teamMemberId",teamMember.getId());
        eventEmitter.fireEvent("tmember.bind",teamMember.getId()+"",String.format("[%s]集团员工档案绑定",teamMember.getId()), JSON.serialize(data),
                new LinkerBean("teammember",teamMember.getName(),teamMember.getId()+""),
                new LinkerBean("member",teamMember.getMember().getNickName(),teamMember.getMemberId()+""));

    }
    @Autowired
    public void setTeamMemberService(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }
    @Autowired
    public void setEventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }
}
