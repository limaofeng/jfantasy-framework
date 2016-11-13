package org.jfantasy.pay.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.pay.bean.OrderType;
import org.springframework.stereotype.Repository;

@Repository
public class OrderTypeDao extends HibernateDao<OrderType,String>{
}
