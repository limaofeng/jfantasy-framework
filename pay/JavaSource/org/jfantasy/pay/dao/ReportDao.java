package org.jfantasy.pay.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.pay.bean.Report;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDao extends HibernateDao<Report, String> {
}
