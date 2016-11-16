package org.jfantasy.member.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.event.TeamInviteEvent;
import org.springframework.stereotype.Component;

/**
 * 当团队添加用户时,发送邀请
 */
@Component
public class TeamMemberInsertListener extends AbstractChangedListener<TeamMember> {

    private static final long serialVersionUID = 4221243459220184177L;

    public TeamMemberInsertListener(){
        super(EventType.POST_COMMIT_INSERT);
    }

    @Override
    public void onPostInsert(TeamMember member, PostInsertEvent event) {
        this.applicationContext.publishEvent(new TeamInviteEvent(member.getTeamId(), member.getMobile()));
    }

}
