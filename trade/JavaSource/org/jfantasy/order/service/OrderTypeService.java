package org.jfantasy.order.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.SpELUtil;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderCashFlow;
import org.jfantasy.order.bean.OrderType;
import org.jfantasy.order.bean.enums.PayeeType;
import org.jfantasy.order.bean.enums.Stage;
import org.jfantasy.order.dao.OrderCashFlowDao;
import org.jfantasy.order.dao.OrderDao;
import org.jfantasy.order.dao.OrderTypeDao;
import org.jfantasy.trade.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderTypeService {

    private final OrderTypeDao orderTypeDao;
    private final OrderDao orderDao;
    private final OrderCashFlowDao orderCashFlowDao;
    private AccountService accountService;

    @Autowired
    public OrderTypeService(OrderTypeDao orderTypeDao, OrderDao orderDao, OrderCashFlowDao orderCashFlowDao) {
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
        this.orderCashFlowDao = orderCashFlowDao;
    }

    @Transactional(readOnly = true)
    public Pager<OrderType> findPager(Pager<OrderType> pager, List<PropertyFilter> filters) {
        return this.orderTypeDao.findPager(pager, filters);
    }

    @Transactional(readOnly = true)
    public List<OrderCashFlow> cashFlow(String id, Stage stage) {
        if (Stage.finished == stage) {
            return this.orderCashFlowDao.find(Restrictions.eq("id", id), Restrictions.eq("stage", stage),Restrictions.isNull("parent"));
        }
        return new ArrayList<>();
    }

    @Transactional
    public OrderType save(OrderType orderType) {
        if (orderType.getEnabled() == null) {
            orderType.setEnabled(true);
        }
        return this.orderTypeDao.save(orderType);
    }

    @Transactional
    public OrderType get(String type) {
        return this.orderTypeDao.get(type);
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

    @Transactional
    public List<OrderType> getAll() {
        return this.orderTypeDao.getAll();
    }

    @Transactional
    public BigDecimal getValue(OrderCashFlow cashFlow, Order order) {
        Expression expression = SpELUtil.getExpression(cashFlow.getValue());
        return expression.getValue(SpELUtil.createEvaluationContext(order), BigDecimal.class);
    }

    @Transactional
    public String getPayee(OrderCashFlow cashFlow, Order order) {
        Expression expression = SpELUtil.getExpression(cashFlow.getValue());
        String payee = expression.getValue(SpELUtil.createEvaluationContext(order), String.class);
        return cashFlow.getPayeeType() == PayeeType.account ? payee : accountService.loadAccountByOwner(payee).getSn();
    }

    public EvaluationContext createEvaluationContext(Order order) {
        Map<String, Object> data = new HashMap<>();
        return SpELUtil.createEvaluationContext(order, data);
    }

    @Transactional
    public List<OrderCashFlow> cashflows(String id) {
        return this.orderCashFlowDao.find(Restrictions.eq("orderType.id", id));
    }

    @Transactional
    public OrderCashFlow save(OrderCashFlow cashFlow) {
        return this.orderCashFlowDao.save(cashFlow);
    }

    public void delete(String id, String fid) {
        OrderCashFlow cashFlow = this.orderCashFlowDao.findUnique(Restrictions.eq("orderType.id", id), Restrictions.eq("id", fid));
        if (cashFlow != null) {
            this.orderCashFlowDao.delete(cashFlow);
        }
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
