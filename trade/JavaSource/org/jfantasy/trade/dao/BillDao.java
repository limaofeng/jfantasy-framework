package org.jfantasy.trade.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.trade.bean.Bill;
import org.springframework.stereotype.Repository;

@Repository
public class BillDao extends HibernateDao<Bill,Long> {
}
