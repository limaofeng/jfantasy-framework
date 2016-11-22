package org.jfantasy.pay.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.pay.bean.Order;
import org.jfantasy.pay.bean.OrderType;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.dao.OrderDao;
import org.jfantasy.pay.dao.OrderTypeDao;
import org.jfantasy.pay.job.OrderClose;
import org.jfantasy.pay.order.OrderServiceFactory;
import org.jfantasy.pay.order.entity.OrderDetails;
import org.jfantasy.pay.order.entity.OrderKey;
import org.jfantasy.pay.order.entity.enums.OrderStatus;
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
    private OrderServiceFactory orderServiceFactory;
    private ScheduleService scheduleService;

    @Autowired
    public OrderService(OrderTypeDao orderTypeDao, OrderDao orderDao) {
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Order get(OrderKey key) {
        return this.orderDao.get(key);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Pager<Order> findPager(Pager<Order> pager, List<PropertyFilter> filters) {
        return this.orderDao.findPager(pager, filters);
    }

    /**
     * 查询 key 对应的订单信息
     *
     * @param key 订单Key
     * @return Order 中包含 OrderDetails 的信息
     */
    @Transactional
    public Order getOrder(OrderKey key) {
        Order order = this.orderDao.get(key);
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
    }

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
    }

    @Transactional
    public void update(Order order) {
        this.orderDao.update(order);
    }

    @Transactional
    public Order close(OrderKey key) {
        Order order = this.orderDao.get(key);
        if (OrderStatus.unpaid != order.getStatus()) {
            throw new ValidationException("order = [" + key + "] 订单已经支付，不能关闭!");
        }
        // 确认第三方支付成功后，修改关闭状态
        Transaction transaction = this.transactionService.getByUniqueId(Transaction.generateUnionid(Project.PAYMENT, order.getKey()));
        transaction.setStatus(TxStatus.close);
        transaction.setStatusText(TxStatus.close.getValue());
        this.transactionService.update(transaction);
        order.setStatus(OrderStatus.close);
        this.scheduleService.removeTrigdger(OrderClose.triggerKey(order));
        return this.orderDao.update(order);
    }

    @Transactional
    public List<Order> find(Criterion... criterions) {
        return this.orderDao.find(criterions);
    }

    @Transactional
    public boolean isExpired(Order order) {
        boolean expired = this.orderTypeDao.isExpired(order.getType(), order.getOrderTime());
        if (expired && this.orderDao.exists(Restrictions.eq("type", order.getType()), Restrictions.eq("sn", order.getSn())) && order.getStatus() == OrderStatus.unpaid) {
            try {
                this.close(OrderKey.newInstance(order.getKey()));
            } catch (ValidationException e) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return expired;
    }

    @Autowired
    public void setOrderServiceFactory(OrderServiceFactory orderServiceFactory) {
        this.orderServiceFactory = orderServiceFactory;
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
