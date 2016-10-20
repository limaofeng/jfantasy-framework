package org.jfantasy.pay.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.pay.bean.Bill;
import org.springframework.stereotype.Repository;

@Repository
public class BillDao extends HibernateDao<Bill,Long> {
}
