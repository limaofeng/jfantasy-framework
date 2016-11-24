package org.jfantasy.trade.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.trade.bean.Project;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDao extends HibernateDao<Project, String> {

}
