package org.jfantasy.member.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.member.bean.Team;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.bean.TeamType;
import org.jfantasy.member.bean.enums.TeamMemberStatus;
import org.jfantasy.member.bean.enums.TeamStatus;
import org.jfantasy.member.dao.TeamDao;
import org.jfantasy.member.dao.TeamMemberDao;
import org.jfantasy.member.dao.TeamTypeDao;
import org.jfantasy.security.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamDao teamDao;
    private final TeamMemberDao teamMemberDao;
    private final TeamTypeDao teamTypeDao;
    private final RoleDao roleDao;

    @Autowired
    public TeamService(TeamDao teamDao, TeamMemberDao teamMemberDao, TeamTypeDao teamTypeDao, RoleDao roleDao) {
        this.teamDao = teamDao;
        this.teamMemberDao = teamMemberDao;
        this.teamTypeDao = teamTypeDao;
        this.roleDao = roleDao;
    }

    public Pager<Team> findPager(Pager<Team> pager, List<PropertyFilter> filters) {
        return teamDao.findPager(pager, filters);
    }

    @Transactional
    public Team save(Team team) {
        TeamType teamType = this.teamTypeDao.get(team.getType().getId());
        TeamMember member = team.getOwner();

        // 保存集团
        team.setStatus(TeamStatus.apply);
        team.setType(teamType);
        this.teamDao.save(team);

        // 保存集团所有者
        member.setTeam(team);
        member.setStatus(TeamMemberStatus.unactivated);
        member.setRole(this.roleDao.get(teamType.getOwnerRole()));
        this.teamMemberDao.save(member);

        // 将所有者更新到集团
        team.setOwnerId(member.getId());
        this.teamDao.update(team);
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
