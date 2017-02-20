package org.jfantasy.member.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.member.bean.MemberType;
import org.springframework.stereotype.Repository;

@Repository
public class MemberTypeDao extends HibernateDao<MemberType, String> {

}
