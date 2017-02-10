package org.jfantasy.security.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.security.bean.Job;
import org.springframework.stereotype.Repository;

@Repository
public class JobDao extends HibernateDao<Job,String> {
}
