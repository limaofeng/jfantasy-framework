package org.jfantasy.order.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.SpELUtil;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.order.bean.*;
import org.jfantasy.order.bean.enums.DataType;
import org.jfantasy.order.bean.enums.PayeeType;
import org.jfantasy.order.bean.enums.Stage;
import org.jfantasy.order.dao.*;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.enums.AccountType;
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
    private final OrderPayeeDao orderPayeeDao;
    private final OrderPriceDao orderPriceDao;
    private AccountService accountService;

    @Autowired
    public OrderTypeService(OrderTypeDao orderTypeDao, OrderDao orderDao, OrderCashFlowDao orderCashFlowDao, ProjectDao projectDao, OrderPayeeDao orderPayeeDao, OrderPriceDao orderPriceDao) {
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
        this.orderCashFlowDao = orderCashFlowDao;
        this.projectDao = projectDao;
        this.orderPayeeDao = orderPayeeDao;
        this.orderPriceDao = orderPriceDao;
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

    @Transactional(readOnly = true)
    public List<OrderPayee> payees(String id) {
        return this.orderPayeeDao.find(Restrictions.eq("orderType.id", id));
    }

    @Transactional(readOnly = true)
    public List<OrderPrice> prices(String id) {
        return this.orderPriceDao.find(Restrictions.eq("orderType.id", id));
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
    public OrderCashFlow cashFlow(String id, String code) {
        return this.orderCashFlowDao.findUnique(Restrictions.eq("orderType.id", id), Restrictions.eq("code", code));
    }

    @Transactional
    public BigDecimal getValue(OrderCashFlow cashFlow, Order order) {
        Expression expression = SpELUtil.getExpression(cashFlow.getValue());
        return expression.getValue(SpELUtil.createEvaluationContext(order), BigDecimal.class);
    }

    @Transactional(readOnly = true)
    public OrderPayee getPayee(String id, String code) {
        return orderPayeeDao.findUnique(Restrictions.eq("orderType.id", id), Restrictions.eq("code", code));
    }

    @Transactional
    public String getPayee(OrderCashFlow cashFlow, Order order) {
        OrderPayee payee = cashFlow.getPayee();
        if (payee.getType() == PayeeType.fixed) {
            return payee.getCode();
        }
        OrderPayeeValue value = ObjectUtil.find(order.getPayees(), "code", cashFlow.getPayee().getCode());
        if (value == null) {
            throw new ValidationException(String.format("订单[%s]缺少[%s]收款人信息", order.getId(), cashFlow.getPayee().getCode()));
        }
        switch (value.getType()) {
            case team:
                return accountService.loadAccountByOwner(AccountType.enterprise, value.getValue()).getSn();
            case member:
                return accountService.loadAccountByOwner(AccountType.personal, value.getValue()).getSn();
            case account:
                return value.getValue();
            default:
                throw new ValidationException(String.format("type = %s 错误", value.getType()));
        }
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
    public OrderPrice save(OrderPrice price) {
        OrderPrice old = price.getOrderType() == null ?
                this.orderPriceDao.findUnique(Restrictions.eq("code", price.getCode()), Restrictions.isNull("orderType")) :
                this.orderPriceDao.findUnique(Restrictions.eq("code", price.getCode()), Restrictions.eq("orderType.id", price.getOrderType().getId()));
        if (price.getDataType() == DataType.reference) {
            OrderPrice reference = price.getReference();
            price.setCode(reference.getCode());
            price.setTitle(reference.getTitle());
        }
        if (old != null) {
            price.setId(old.getId());
        }
        return this.orderPriceDao.save(price);
    }

    @Transactional
    public OrderPayee save(OrderPayee payee) {
        OrderPayee old = payee.getOrderType() == null ?
                this.orderPayeeDao.findUnique(Restrictions.eq("code", payee.getCode()), Restrictions.isNull("orderType")) :
                this.orderPayeeDao.findUnique(Restrictions.eq("code", payee.getCode()), Restrictions.eq("orderType.id", payee.getOrderType().getId()));
        if (payee.getDataType() == DataType.reference) {
            OrderPayee reference = payee.getReference();
            payee.setType(reference.getType());
            payee.setCode(reference.getCode());
            payee.setTitle(reference.getTitle());
        }
        if (old != null) {
            payee.setId(old.getId());
        }
        return this.orderPayeeDao.save(payee, true);
    }

    @Transactional
    public void save(String id, OrderPayee... payees) {
        for (OrderPayee payee : payees) {
            payee.setOrderType(this.orderTypeDao.get(id));
            this.save(payee);
        }
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
            cashFlow.setLayer(1);
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
