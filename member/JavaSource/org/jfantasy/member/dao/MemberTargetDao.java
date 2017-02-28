package org.jfantasy.member.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.member.bean.MemberTarget;
import org.jfantasy.member.bean.MemberTargetKey;
import org.springframework.stereotype.Repository;

@Repository
public class MemberTargetDao extends HibernateDao<MemberTarget, MemberTargetKey> {
}
