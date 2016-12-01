package org.jfantasy.invoice.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.invoice.bean.InvoiceOrder;
import org.jfantasy.invoice.dao.InvoiceOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvoiceOrderService {

    private final InvoiceOrderDao invoiceOrderDao;

    @Autowired
    public InvoiceOrderService(InvoiceOrderDao invoiceOrderDao) {
        this.invoiceOrderDao = invoiceOrderDao;
    }

    public Pager<InvoiceOrder> findPager(Pager<InvoiceOrder> pager, List<PropertyFilter> filters) {
        return invoiceOrderDao.findPager(pager, filters);
    }

    @Transactional
    public InvoiceOrder save(InvoiceOrder order) {
        InvoiceOrder invoiceOrder = this.invoiceOrderDao.findUnique(Restrictions.eq("orderType", order.getOrderType()), Restrictions.eq("orderSn", order.getOrderSn()));
        if (invoiceOrder == null) {
            order.setStatus(InvoiceOrder.InvoiceOrderStatus.NONE);
            return this.invoiceOrderDao.save(order);
        }
        return invoiceOrder;
    }

}
