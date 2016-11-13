package org.jfantasy.pay.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.pay.bean.OrderType;
import org.jfantasy.pay.dao.OrderDao;
import org.jfantasy.pay.dao.OrderTypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderTypeService {

    private final OrderTypeDao orderTypeDao;
    private final OrderDao orderDao;

    @Autowired
    public OrderTypeService(OrderTypeDao orderTypeDao, OrderDao orderDao) {
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
    }

    @Transactional(readOnly = true)
    public Pager<OrderType> findPager(Pager<OrderType> pager, List<PropertyFilter> filters) {
        return this.orderTypeDao.findPager(pager, filters);
    }

    @Transactional
    public OrderType save(OrderType orderType) {
        if (orderType.getEnabled() == null) {
            orderType.setEnabled(true);
        }
        return this.orderTypeDao.save(orderType);
    }

    @Transactional
    public OrderType update(OrderType orderType, boolean has) {
        return this.orderTypeDao.update(orderType, has);
    }

    @Transactional
    public void delete(String id) {
        if (this.orderDao.count(Restrictions.eq("type", id)) != 0) {
            throw new ValidationException("该订单类型下有订单，不能直接删除！");
        }
        this.orderTypeDao.delete(id);
    }

}
