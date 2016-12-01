package org.jfantasy.logistics.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.logistics.bean.Logistics;
import org.springframework.stereotype.Repository;

@Repository
public class LogisticsDao extends HibernateDao<Logistics, String> {
}
