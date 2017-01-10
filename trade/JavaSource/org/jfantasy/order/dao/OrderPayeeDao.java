package org.jfantasy.order.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.order.bean.OrderPayee;
import org.springframework.stereotype.Repository;

@Repository
public class OrderPayeeDao extends HibernateDao<OrderPayee,Long> {
}
