package org.jfantasy.order.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.SpELUtil;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderCashFlow;
import org.jfantasy.order.bean.OrderType;
import org.jfantasy.order.bean.enums.PayeeType;
import org.jfantasy.order.bean.enums.Stage;
import org.jfantasy.order.dao.OrderCashFlowDao;
import org.jfantasy.order.dao.OrderDao;
import org.jfantasy.order.dao.OrderTypeDao;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.dao.ProjectDao;
import org.jfantasy.trade.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderTypeService {

    private final OrderTypeDao orderTypeDao;
    private final ProjectDao projectDao;
    private final OrderDao orderDao;
    private final OrderCashFlowDao orderCashFlowDao;
    private AccountService accountService;

    @Autowired
    public OrderTypeService(OrderTypeDao orderTypeDao, OrderDao orderDao, OrderCashFlowDao orderCashFlowDao, ProjectDao projectDao) {
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
        this.orderCashFlowDao = orderCashFlowDao;
        this.projectDao = projectDao;
    }

    @Transactional(readOnly = true)
    public Pager<OrderType> findPager(Pager<OrderType> pager, List<PropertyFilter> filters) {
        return this.orderTypeDao.findPager(pager, filters);
    }

    @Transactional(readOnly = true)
    public List<OrderCashFlow> cashflows(String id, Stage stage) {
        if (Stage.finished == stage) {
            return this.orderCashFlowDao.find(new Criterion[]{Restrictions.eq("orderType.id", id), Restrictions.eq("stage", stage), Restrictions.isNull("parent")}, "sort", "asc");
        }
        return new ArrayList<>();
    }

    @Transactional
    public List<OrderCashFlow> cashflows(String id) {
        return this.orderCashFlowDao.find(new Criterion[]{Restrictions.eq("orderType.id", id)}, "sort", "asc");
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
    public OrderCashFlow cashFlow(String id) {
        return this.orderCashFlowDao.findUnique(Restrictions.eq("code", id));
    }

    @Transactional
    public BigDecimal getValue(OrderCashFlow cashFlow, Order order) {
        Expression expression = SpELUtil.getExpression(cashFlow.getValue());
        return expression.getValue(SpELUtil.createEvaluationContext(order), BigDecimal.class);
    }

    @Transactional
    public String getPayee(OrderCashFlow cashFlow, Order order) {
        Expression expression = SpELUtil.getExpression(cashFlow.getPayee());
        String payee = expression.getValue(SpELUtil.createEvaluationContext(order), String.class);
        return cashFlow.getPayeeType() == PayeeType.account ? payee : accountService.loadAccountByOwner(payee).getSn();
    }

    @Transactional
    public OrderCashFlow save(OrderCashFlow cashFlow) {
        Project project = this.projectDao.get(cashFlow.getProject());
        cashFlow.setProjectName(project.getName());
        List<OrderCashFlow> categories = siblings(cashFlow);
        // 新增数据
        cashFlow.setSort(categories.size() + 1);
        return this.orderCashFlowDao.save(cashFlow);
    }

    @Transactional
    public OrderCashFlow update(String id, Long fid, OrderCashFlow cashFlow) {
        List<OrderCashFlow> cashflows = this.orderCashFlowDao.find(Restrictions.eq("orderType.id", id));
        OrderCashFlow old = ObjectUtil.find(cashflows, "id", fid);
        if (old == null) {
            return null;
        }
        Project project = this.projectDao.get(cashFlow.getProject());
        cashFlow.setProjectName(project.getName());
        cashFlow.setId(fid);
        cashFlow.setOrderType(old.getOrderType());

        List<OrderCashFlow> cashFlows = siblings(cashFlow);
        if (cashFlow.getSort() != null) {
            resort(cashFlow, cashFlows, old);
        }

        return this.orderCashFlowDao.update(cashFlow);
    }

    private List<OrderCashFlow> siblings(OrderCashFlow cashFlow) {
        if (cashFlow.getParent() == null || StringUtil.isBlank(cashFlow.getParent().getId())) {
            cashFlow.setLayer(0);
            cashFlow.setParent(null);
            return ObjectUtil.sort(orderCashFlowDao.find(Restrictions.eq("orderType.id", cashFlow.getOrderType().getId()), Restrictions.isNull("parent")), "sort", "asc");
        } else {
            OrderCashFlow parentCashFlow = this.orderCashFlowDao.get(cashFlow.getParent().getId());
            cashFlow.setLayer(parentCashFlow.getLayer() + 1);
            return ObjectUtil.sort(orderCashFlowDao.find(Restrictions.eq("orderType.id", cashFlow.getOrderType().getId()), Restrictions.eq("parent.id", parentCashFlow.getId())), "sort", "asc");
        }
    }

    private void resort(OrderCashFlow cashFlow, List<OrderCashFlow> cashFlows, OrderCashFlow old) {
        if (ObjectUtil.find(cashFlows, "id", old.getId()) == null) {// 移动了节点的层级
            int i = 0;
            for (OrderCashFlow m : ObjectUtil.sort((old.getParent() == null || StringUtil.isBlank(old.getParent().getId())) ? orderCashFlowDao.find(Restrictions.eq("orderType.id", cashFlow.getOrderType().getId()), Restrictions.isNull("parent")) : orderCashFlowDao.find(Restrictions.eq("orderType.id", cashFlow.getOrderType().getId()), Restrictions.eq("parent.id", old.getParent().getId())), "sort", "asc")) {
                m.setSort(i++);
                this.orderCashFlowDao.save(m);
            }
            cashFlows.add(cashFlow.getSort() - 1, cashFlow);
        } else if (!old.getSort().equals(cashFlow.getSort())) {
            OrderCashFlow t = ObjectUtil.remove(cashFlows, "id", old.getId());
            if (cashFlows.size() >= cashFlow.getSort()) {
                cashFlows.add(cashFlow.getSort() - 1, t);
            } else {
                cashFlows.add(t);
            }
        }
        // 重新排序后更新新的位置
        for (int i = 0; i < cashFlows.size(); i++) {
            OrderCashFlow m = cashFlows.get(i);
            if (m.getId().equals(cashFlow.getId())) {
                continue;
            }
            m.setSort(i + 1);
            this.orderCashFlowDao.save(m);
        }
    }

    public void delete(String id, Long fid) {
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
