package org.jfantasy.invoice.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.BeanUtil;
import org.jfantasy.invoice.bean.Invoice;
import org.jfantasy.invoice.bean.InvoiceItem;
import org.jfantasy.invoice.bean.enums.InvoiceStatus;
import org.jfantasy.invoice.dao.InvoiceDao;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceService {

    private final InvoiceDao invoiceDao;
    private final OrderDao orderDao;

    @Autowired
    public InvoiceService(InvoiceDao invoiceDao, OrderDao orderDao) {
        this.invoiceDao = invoiceDao;
        this.orderDao = orderDao;
    }

    public Pager<Invoice> findPager(Pager<Invoice> pager, List<PropertyFilter> filters) {
        return this.invoiceDao.findPager(pager, filters);
    }

    @Transactional
    public List<Invoice> save(Invoice invoice) {
        Map<Long, Invoice> invoices = new HashMap<>();
        for (InvoiceItem item : invoice.getItems()) {
            Order order = orderDao.get(item.getOrder().getId());
            if (order == null) {
                throw new ValidationException(100000, "订单信息不存在");
            }
            if (order.getInvoiceStatus() != org.jfantasy.order.bean.enums.InvoiceStatus.wait) {
                throw new ValidationException(100000, "订单已经申请开票,不能重复申请");
            }
            if (!order.getMemberId().equals(invoice.getMemberId())) {
                throw new ValidationException(100000, "只能为自己的订单申请发票");
            }
            //自动拆单逻辑
            Long drawer = order.getPayee();
            Invoice tinvoice = invoices.get(drawer);
            if (tinvoice == null) {
                tinvoice = BeanUtil.copyProperties(new Invoice(), invoice);
                invoices.put(drawer, tinvoice);
                tinvoice.setDrawer(drawer);
                tinvoice.setAmount(BigDecimal.ZERO);
            }
            //设置发票
            item.setInvoice(tinvoice);
            item.setOrder(order);
            //更新订单状态
            order.setInvoiceStatus(org.jfantasy.order.bean.enums.InvoiceStatus.submitted);
            this.orderDao.save(order);
            //开票金额
            tinvoice.setAmount(tinvoice.getAmount().add(order.getPayableAmount()));
        }

        //保存发票
        for (Map.Entry<Long, Invoice> entry : invoices.entrySet()) {
            Invoice tinvoice = entry.getValue();
            tinvoice.setAmount(tinvoice.getAmount().setScale(2, BigDecimal.ROUND_UP));
            tinvoice.setStatus(InvoiceStatus.NONE);
            this.invoiceDao.save(tinvoice);
        }
        return new ArrayList<>(invoices.values());
    }

    @Transactional
    public Invoice update(Invoice invoice) {
        Invoice tinvoice = this.get(invoice.getId());
        if (tinvoice.getStatus() == InvoiceStatus.COMPLETE || invoice.getStatus() == InvoiceStatus.NONE) {
            throw new ValidationException(100000, "发票状态不符,不允许修改");
        }
        return this.invoiceDao.update(invoice, true);
    }

    @Transactional
    public void deltele(Long... ids) {
        for (Long id : ids) {
            this.invoiceDao.delete(id);
        }
    }

    public Invoice get(Long id) {
        return this.invoiceDao.get(id);
    }

}
