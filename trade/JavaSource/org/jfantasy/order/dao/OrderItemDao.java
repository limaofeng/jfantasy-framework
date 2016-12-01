package org.jfantasy.order.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.order.bean.OrderItem;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemDao extends HibernateDao<OrderItem,Long> {

}