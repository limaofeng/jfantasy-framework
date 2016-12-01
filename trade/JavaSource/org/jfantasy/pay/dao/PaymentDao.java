package org.jfantasy.pay.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.pay.bean.Payment;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao extends HibernateDao<Payment, String> {
}
