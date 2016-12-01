package org.jfantasy.order.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.order.bean.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao extends HibernateDao<Order,String> {

}
