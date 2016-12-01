package org.jfantasy.order.dao;


import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.order.bean.OrderServer;
import org.springframework.stereotype.Repository;

@Repository
public class OrderServerDao extends HibernateDao<OrderServer, String> {
}
