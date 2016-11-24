package org.jfantasy.trade.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.trade.bean.Point;
import org.springframework.stereotype.Repository;

@Repository
public class PointDao extends HibernateDao<Point, Long> {
}
