package org.jfantasy.order.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.order.bean.OrderPayeeValue;
import org.springframework.stereotype.Repository;

@Repository
public class OrderPayeeValueDao extends HibernateDao<OrderPayeeValue,Long> {
}
