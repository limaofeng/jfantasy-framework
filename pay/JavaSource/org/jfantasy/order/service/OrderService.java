package org.jfantasy.order.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.order.bean.Order;
import org.jfantasy.trade.service.TransactionService;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.order.dao.OrderDao;
import org.jfantasy.order.dao.OrderTypeDao;
import org.jfantasy.order.job.OrderClose;
import org.jfantasy.order.OrderServiceFactory;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单明细记录
 */
@Service
public class OrderService {

    private static final Log LOG = LogFactory.getLog(OrderService.class);

    private final OrderDao orderDao;
    private final OrderTypeDao orderTypeDao;
    private TransactionService transactionService;
    private ScheduleService scheduleService;

    @Autowired
    public OrderService(OrderTypeDao orderTypeDao, OrderDao orderDao) {
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Order get(String id) {
        return this.orderDao.get(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Pager<Order> findPager(Pager<Order> pager, List<PropertyFilter> filters) {
        return this.orderDao.findPager(pager, filters);
    }

    /**
     * 查询 key 对应的订单信息
     *
     * @param id 订单ID
     * @return Order 中包含 OrderDetails 的信息
    @Transactional
    @Deprecated
    public Order getOrder(String id) {
        Order order = this.orderDao.get(id);
        if (order == null) {
            if (!orderServiceFactory.containsType(key.getType())) {
                throw new ValidationException("订单类型[" + key.getType() + "] 对应的 PaymentOrderService 未配置！");
            }
            //获取订单信息
            OrderType orderType = orderTypeDao.get(key.getType());
            if (orderType == null || !orderType.getEnabled()) {
                throw new ValidationException("支付系统不能处理该类型的订单[" + key.getType() + "]，请检查或者联系开发人员!");
            }
            OrderDetails orderDetails = orderServiceFactory.getOrderService(key.getType()).loadOrder(key);
            if (orderDetails == null) {
                throw new ValidationException("订单不存在,请核对后,再继续操作!");
            }
            order = conversion(orderDetails);
            if (order.isExpired()) {
                throw new ValidationException("订单已经超出支付期限!");
            }
            order = this.orderDao.save(order);
        }
        return order;
    }*/

    /*
    private Order conversion(OrderDetails details) {
        Order order = new Order();
        order.setSn(details.getSn());
        order.setType(details.getType());
        order.setSubject(details.getSubject());
        order.setBody(details.getBody());
        order.setPayableFee(details.getPayableFee());
        order.setTotalFee(details.getTotalFee());
        order.setStatus(OrderStatus.unpaid);
        order.setOrderItems(details.getOrderItems());
        order.setMemberId(details.getMemberId());
        order.setProperties(details.getProperties());
        order.setOrderTime(DateUtil.parse(order.get("order_time").toString()));
        return order;
    }*/

    @Transactional
    public void update(Order order) {
        this.orderDao.update(order);
    }

    @Transactional
    public Order close(String id) {
        Order order = this.orderDao.get(id);
        if (OrderStatus.UNPAID != order.getStatus()) {
            throw new ValidationException("order = [" + id + "] 订单已经支付，不能关闭!");
        }
        // 确认第三方支付成功后，修改关闭状态
        Transaction transaction = this.transactionService.getByUniqueId(Transaction.generateUnionid(Project.PAYMENT, order.getId()));
        transaction.setStatus(TxStatus.close);
        transaction.setStatusText(TxStatus.close.getValue());
        this.transactionService.update(transaction);
        order.setStatus(OrderStatus.CLOSE);
        this.scheduleService.removeTrigdger(OrderClose.triggerKey(order));
        return this.orderDao.update(order);
    }

    @Transactional
    public List<Order> find(Criterion... criterions) {
        return this.orderDao.find(criterions);
    }

    @Transactional
    public boolean isExpired(Order order) {
        boolean expired = this.orderTypeDao.isExpired(order.getType(), order.getCreateTime());
        if (expired && order.getStatus() == OrderStatus.UNPAID) {
            try {
                this.close(order.getId());
            } catch (ValidationException e) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return expired;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

}
