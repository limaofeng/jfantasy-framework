package org.jfantasy.member.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.member.bean.TeamType;
import org.springframework.stereotype.Repository;


@Repository
public class TeamTypeDao extends HibernateDao<TeamType, String> {
}
