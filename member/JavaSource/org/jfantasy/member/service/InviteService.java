package org.jfantasy.member.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.member.bean.TeamInvite;
import org.jfantasy.member.bean.Team;
import org.jfantasy.member.dao.InviteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InviteService {

    @Autowired
    private InviteDao inviteDao;

    public Pager<TeamInvite> findPager(Pager<TeamInvite> pager, List<PropertyFilter> filters) {
        return this.inviteDao.findPager(pager, filters);
    }

    @Transactional
    public void deltele(Long... ids) {
        for (Long id : ids) {
            this.inviteDao.delete(id);
        }
    }

    public TeamInvite get(Long id) {
        return this.inviteDao.get(id);
    }

    @Transactional
    public List<TeamInvite> save(String teamid, List<TeamInvite> teamInvites) {
        for (int i = 0, size = teamInvites.size(); i < size; i++) {
            TeamInvite teamInvite = teamInvites.get(i);
            teamInvite.setTeam(Team.newInstance(teamid));
            teamInvites.set(i, this.save(teamInvite));
        }
        return teamInvites;
    }

    @Transactional
    public TeamInvite save(TeamInvite teamInvite) {
        return this.inviteDao.save(teamInvite);
    }

}
