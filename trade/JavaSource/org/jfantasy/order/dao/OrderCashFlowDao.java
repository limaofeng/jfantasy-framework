package org.jfantasy.order.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.order.bean.OrderCashFlow;
import org.springframework.stereotype.Repository;

@Repository
public class OrderCashFlowDao extends HibernateDao<OrderCashFlow,Long> {
}
