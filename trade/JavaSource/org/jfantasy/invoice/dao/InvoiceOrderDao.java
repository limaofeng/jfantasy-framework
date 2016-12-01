package org.jfantasy.invoice.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.invoice.bean.InvoiceOrder;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceOrderDao extends HibernateDao<InvoiceOrder, Long> {
}
