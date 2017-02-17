package org.jfantasy.member.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.member.bean.Team;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.dao.TeamDao;
import org.jfantasy.member.dao.TeamMemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamDao teamDao;
    private final TeamMemberDao teamMemberDao;

    @Autowired
    public TeamService(TeamDao teamDao, TeamMemberDao teamMemberDao) {
        this.teamDao = teamDao;
        this.teamMemberDao = teamMemberDao;
    }

    public Pager<Team> findPager(Pager<Team> pager, List<PropertyFilter> filters) {
        return teamDao.findPager(pager, filters);
    }

    @Transactional
    public Team save(Team team) {
        TeamMember member = team.getOwner();
        this.teamDao.save(team);
        member.setTeam(team);
        this.teamMemberDao.save(member);
        return team;
    }

    @Transactional
    public Team update(Team team, boolean patch) {
        return this.teamDao.update(team, patch);
    }

    @Transactional
    public Team owner(String id, Long tmid) {
        Team team = this.teamDao.get(id);
        team.setOwner(this.teamMemberDao.get(tmid));
        return this.teamDao.update(team);
    }

    @Transactional
    public void deltele(String... ids) {
        for (String id : ids) {
            this.teamDao.delete(id);
        }
    }

    public Team get(String id) {
        return this.teamDao.get(id);
    }

}
