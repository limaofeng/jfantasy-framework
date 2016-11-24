package org.jfantasy.trade.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.trade.bean.Report;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDao extends HibernateDao<Report, String> {
}
