package org.jfantasy.logistics.dao;

import org.springframework.stereotype.Repository;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.logistics.bean.DeliveryType;

@Repository
public class DeliveryTypeDao extends HibernateDao<DeliveryType, Long> {

}
