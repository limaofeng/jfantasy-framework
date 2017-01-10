package org.jfantasy.order.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.order.bean.OrderPriceValue;
import org.springframework.stereotype.Repository;

@Repository
public class OrderPriceValueDao extends HibernateDao<OrderPriceValue, Long> {
}
