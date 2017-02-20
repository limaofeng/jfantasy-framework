package org.jfantasy.member.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.member.bean.Enterprise;
import org.springframework.stereotype.Repository;

@Repository
public class EnterpriseDao extends HibernateDao<Enterprise, Long> {

}
