package org.jfantasy.order.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.order.bean.OrderPrice;
import org.springframework.stereotype.Repository;

@Repository
public class OrderPriceDao extends HibernateDao<OrderPrice, Long> {
}
