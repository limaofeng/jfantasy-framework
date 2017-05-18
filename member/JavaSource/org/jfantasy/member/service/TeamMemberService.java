package org.jfantasy.member.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.BeanUtil;
import org.jfantasy.member.bean.Team;
import org.jfantasy.member.bean.TeamMember;
import org.jfantasy.member.bean.enums.TeamMemberStatus;
import org.jfantasy.member.dao.TeamDao;
import org.jfantasy.member.dao.TeamMemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamMemberService {

    private final TeamMemberDao teamMemberDao;
    private final TeamDao teamDao;

    @Autowired
    public TeamMemberService(TeamMemberDao teamMemberDao, TeamDao teamDao) {
        this.teamMemberDao = teamMemberDao;
        this.teamDao = teamDao;
    }

    public Pager<TeamMember> findPager(Pager<TeamMember> pager, List<PropertyFilter> filters) {
        return teamMemberDao.findPager(pager, filters);
    }

    @Transactional(readOnly = true)
    public TeamMember get(Long id) {
        return this.teamMemberDao.get(id);
    }

    @Transactional
    public void deltele(Long... ids) {
        this.teamMemberDao.delete(ids);
    }

    public TeamMember findUnique(String teamId, Long memberId) {
        return this.teamMemberDao.findUnique(Restrictions.eq("member.id", memberId), Restrictions.eq("team.key", teamId));
    }

    /**
     * 根据身份证获取
     * @param idCard 身份证
     * @return TeamMember
     */
    @Transactional
    public TeamMember findByIdCard(String idCard) {
        List<TeamMember> list = this.teamMemberDao.findBy("papersNumber", idCard,"status","asc");
        return list.isEmpty() ? null : list.get(0);
    }
    public TeamMember findUnique(String teamId, String mobile) {
        return this.teamMemberDao.findUnique(Restrictions.eq("mobile", mobile), Restrictions.eq("team.key", teamId));
    }

    @Transactional
    public TeamMember update(TeamMember member, boolean patch) {
        if (!patch) {
            TeamMember oldMember = this.teamMemberDao.get(member.getId());
            return this.teamMemberDao.update(BeanUtil.copyProperties(oldMember, member, "id", "status", "member", "team"));
        }
        return this.teamMemberDao.update(member, true);
    }

    @Transactional
    public TeamMember save(TeamMember member) {
        member.setStatus(TeamMemberStatus.unactivated);
        Team team = this.teamDao.get(member.getTeamId());
        if (team == null) {
            throw new ValidationException(100000, "团队不存在");
        }
        TeamMember teamMember = this.teamMemberDao.findUnique(Restrictions.eq("mobile", member.getMobile()), Restrictions.eq("team.key", member.getTeamId()));
        if (teamMember != null) {
            if (teamMember.getStatus() != TeamMemberStatus.drop) {
                throw new ValidationException(100000, "已经添加到团队,请勿重复添加");
            }
            teamMember.setStatus(TeamMemberStatus.unactivated);
            return this.teamMemberDao.update(BeanUtil.copyProperties(teamMember, member, "member", "id", "team", "status"));
        }
        return this.teamMemberDao.save(member);
    }

    public List<TeamMember> find(Criterion... criterions) {
        return this.teamMemberDao.find(criterions);
    }

    public List<TeamMember> find(List<PropertyFilter> filters) {
        return this.teamMemberDao.find(filters);
    }

    @Transactional
    public void activate(Long id, Long memberId) {
        TeamMember teamMember = this.teamMemberDao.get(id);
        if (teamMember.getStatus() != TeamMemberStatus.unactivated) {
            return;
        }
        teamMember.setMemberId(memberId);
        teamMember.setStatus(TeamMemberStatus.activated);
        this.teamMemberDao.update(teamMember);
        // 添加集团标签到用户
    }

}
