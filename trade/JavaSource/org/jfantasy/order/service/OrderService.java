package org.jfantasy.order.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.HandlebarsTemplateUtils;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.NumberUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.logistics.bean.DeliveryType;
import org.jfantasy.logistics.service.DeliveryTypeService;
import org.jfantasy.member.bean.Receiver;
import org.jfantasy.member.service.ReceiverService;
import org.jfantasy.order.bean.*;
import org.jfantasy.order.bean.enums.InvoiceStatus;
import org.jfantasy.order.bean.enums.OrderFlow;
import org.jfantasy.order.bean.enums.PayeeType;
import org.jfantasy.order.bean.enums.Stage;
import org.jfantasy.order.dao.OrderDao;
import org.jfantasy.order.dao.OrderPayeeValueDao;
import org.jfantasy.order.dao.OrderPriceValueDao;
import org.jfantasy.order.dao.OrderTypeDao;
import org.jfantasy.order.entity.OrderDTO;
import org.jfantasy.order.entity.OrderItemDTO;
import org.jfantasy.order.entity.OrderPayeeDTO;
import org.jfantasy.order.entity.OrderPriceDTO;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.entity.enums.PaymentStatus;
import org.jfantasy.order.entity.enums.ShippingStatus;
import org.jfantasy.order.job.OrderClose;
import org.jfantasy.order.rest.models.ProfitChain;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.rest.models.OrderTransaction;
import org.jfantasy.schedule.service.ScheduleService;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.service.AccountService;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 订单明细记录
 */
@Service
public class OrderService {

    private static final Log LOG = LogFactory.getLog(OrderService.class);

    private final OrderDao orderDao;
    private final OrderTypeDao orderTypeDao;
    private final OrderPriceValueDao orderPriceValueDao;
    private final OrderPayeeValueDao orderPayeeValueDao;
    private TransactionService transactionService;
    private ReceiverService receiverService;
    private ScheduleService scheduleService;
    private DeliveryTypeService deliveryTypeService;
    private AccountService accountService;
    private OrderTypeService orderTypeService;

    @Autowired
    public OrderService(OrderTypeDao orderTypeDao, OrderDao orderDao, OrderPriceValueDao orderPriceValueDao, OrderPayeeValueDao orderPayeeValueDao) {
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
        this.orderPriceValueDao = orderPriceValueDao;
        this.orderPayeeValueDao = orderPayeeValueDao;
    }

    @Transactional(readOnly = true)
    public Order get(String id) {
        return this.orderDao.get(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Pager<Order> findPager(Pager<Order> pager, List<PropertyFilter> filters) {
        return this.orderDao.findPager(pager, filters);
    }

    public void save(OrderPriceValue value){
        this.orderPriceValueDao.save(value);
    }

    public void save(OrderPayeeValue value){
        this.orderPayeeValueDao.save(value);
    }

    @Transactional
    public void update(Order order) {
        this.orderDao.update(order);
    }

    @Transactional
    public void complete(String id) {
        Order order = this.orderDao.get(id);
        if (OrderStatus.paid != order.getStatus()) {
            throw new ValidationException("order = [" + id + "] 订单未支付，不能直接完成!");
        }
        // 更新订单状态为完成
        order.setStatus(OrderStatus.complete);
        order.setCompletionTime(DateUtil.now());
        // 更新发票状态
        if (!NumberUtil.isEquals(BigDecimal.ZERO, order.getTotalAmount()) && !"walletpay".equals(order.getPaymentConfig().getPayProductId())) {// 非钱包支付，可以开发票
            order.setInvoiceStatus(InvoiceStatus.wait);
        }
        this.orderDao.update(order);
    }

    @Transactional
    public Order close(String id) {
        Order order = this.orderDao.get(id);
        if (!(OrderStatus.unpaid == order.getStatus() || OrderStatus.refunded == order.getStatus())) {
            throw new ValidationException("[" + id + "] 只有未支付及已经退款的订单，才能关闭!");
        }
        // 确认第三方支付成功后，修改关闭状态
        Transaction transaction = this.transactionService.getByUniqueId(Transaction.generateUnionid(Project.PAYMENT, order.getId()));
        if (transaction != null) {
            transaction.setStatus(TxStatus.close);
            transaction.setFlowStatus(-1);
            transaction.setStatusText(TxStatus.close.getValue());
            this.transactionService.update(transaction);
        }
        order.setStatus(OrderStatus.closed);
        this.scheduleService.removeTrigdger(OrderClose.triggerKey(order));
        return this.orderDao.update(order);
    }

    @Transactional
    public List<Order> find(Criterion... criterions) {
        return this.orderDao.find(criterions);
    }

    @Transactional
    public List<ProfitChain> profitChains(String id) {
        Order order = this.orderDao.get(id);
        return order.getProfitChains();
    }

    @Transactional
    public boolean isExpired(Order order) {
        boolean expired = this.orderTypeDao.isExpired(order.getType(), order.getCreateTime());
        if (expired && order.getStatus() == OrderStatus.unpaid) {
            try {
                this.close(order.getId());
            } catch (ValidationException e) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return expired;
    }

    @Transactional
    public long getExpires(Order order) {
        return this.orderTypeDao.getExpires(order.getType(), order.getCreateTime());
    }

    @Transactional
    public String getRedirectUrl(Order order) {
        OrderType orderType = this.orderTypeDao.get(order.getType());
        return HandlebarsTemplateUtils.processTemplateIntoString(orderType.getRedirectUrl(), order);
    }

    @Transactional
    public String getSubject(Order order) {
        OrderType orderType = this.orderTypeDao.get(order.getType());
        return HandlebarsTemplateUtils.processTemplateIntoString(orderType.getSubject(), order);
    }

    @Transactional
    public String getBody(Order order) {
        OrderType orderType = this.orderTypeDao.get(order.getType());
        return HandlebarsTemplateUtils.processTemplateIntoString(orderType.getBody(), order);
    }

    @Transactional
    public Order get(OrderTargetKey key) {
        return findUnique(key.getType(), key.getSn());
    }

    @Transactional
    public Order findUnique(String targetType, String targetId) {
        return this.orderDao.findUnique(Restrictions.eq("detailsType", targetType), Restrictions.eq("detailsId", targetId));
    }

    @Transactional
    public Order payment(String id) {
        Map<String, Object> data = new HashMap<>();
        // 订单
        Order order = this.orderDao.get(id);
        if (NumberUtil.isEquals(BigDecimal.ZERO, order.getTotalAmount())) {// 0 元
            throw new ValidationException("0元订单不能创建交易记录");
        }
        // 保存到交易表的数据
        data.putAll(order.getAttrs());
        data.put(Transaction.ORDER_ID, order.getId());
        data.put(Transaction.ORDER_TYPE, order.getType());
        if (order.getStatus() != OrderStatus.unpaid) {
            throw new ValidationException("订单" + order.getStatus().getValue() + ",不能支付");
        }
        Account from = accountService.loadAccountByOwner(order.getMemberId().toString());// 付款方 - 只能是用户自己付款
        Transaction transaction = this.transactionService.payment(from.getSn(), order.getPayableAmount(), "", data);
        order.setPaymentTransaction(transaction);
        return this.orderDao.update(order);
    }

    public Order updatePaymentStatus(Payment payment) {
        Order order = payment.getOrder();
        Transaction transaction = payment.getTransaction();
        PayConfig payConfig = payment.getPayConfig();
        // 更新交易状态
        transaction.setStatus(TxStatus.success);
        transaction.setFlowStatus(9);
        transaction.setStatusText(TxStatus.success.getValue());
        transaction.setPayConfigName(payConfig.getName());
        transactionService.update(transaction);
        // 更新订单状态
        order.setStatus(OrderStatus.paid);
        order.setPaymentStatus(org.jfantasy.order.entity.enums.PaymentStatus.paid);
        order.setPaymentTime(payment.getTradeTime());
        order.setPaymentConfig(payConfig);
        order.setPayConfigName(payConfig.getName());
        // 查询付款人信息
        Account account = accountService.get(transaction.getFrom());
        order.setPayer(Long.valueOf(account.getOwner()));
        return this.orderDao.update(order);
    }

    @Transactional
    public Order refund(String id, BigDecimal refundAmount, String note) {
        Order order = this.orderDao.get(id);// 订单
        if (order.getStatus() == OrderStatus.refunding || order.getStatus() == OrderStatus.refunded || order.getStatus() == OrderStatus.closed) {
            return order;
        }
        if (NumberUtil.isEquals(BigDecimal.ZERO, order.getTotalAmount())) {// 0 元
            throw new ValidationException("0元订单不能创建交易记录");
        }
        if (refundAmount.compareTo(order.getTotalAmount()) > 0) {
            throw new ValidationException("退款金额不能大于订单金额");
        }
        if (NumberUtil.isEquals(BigDecimal.ZERO, refundAmount)) {// 0 元退款
            order.setStatus(OrderStatus.refunded);
            this.orderDao.update(order);
            return this.close(id);
        }
        if (order.getStatus() != OrderStatus.paid) {
            throw new ValidationException("订单" + order.getStatus().getValue() + ",不能继续退款");
        }
        order.setStatus(OrderStatus.refunding);

        Transaction original = this.transactionService.getByUnionId(Transaction.generateUnionid(OrderTransaction.Type.payment.getValue(), order.getId()));
        // 匹配旧的逻辑
        if (original == null) {
            original = this.transactionService.getByUnionId(Transaction.generateUnionid(OrderTransaction.Type.payment.getValue(), order.getDetailsId()));
        }
        if (original == null) {
            original = this.transactionService.getByUnionId(Transaction.generateUnionid(OrderTransaction.Type.payment.getValue(), order.getDetailsType() + ":" + order.getDetailsId()));
        }
        Transaction transaction = this.transactionService.refund(original, refundAmount, note);
        order.setRefundTransaction(transaction);
        return this.orderDao.update(order);
    }

    public Order updateRefundStatus(Refund refund) {
        Order order = refund.getOrder();
        Transaction transaction = refund.getTransaction();
        PayConfig payConfig = refund.getPayConfig();
        // 更新交易状态
        transaction.setStatus(TxStatus.success);
        transaction.setFlowStatus(9);
        transaction.setStatusText(TxStatus.success.getValue());
        transaction.setPayConfigName(payConfig.getName());
        transactionService.update(transaction);
        // 更新订单状态
        order.setStatus(OrderStatus.refunded);
        order.setPaymentStatus(order.getPayableAmount().equals(refund.getTotalAmount()) ? PaymentStatus.refunded : PaymentStatus.partRefund);
        order.setRefundAmount(refund.getTotalAmount());
        order.setRefundTime(refund.getTradeTime());
        return this.orderDao.update(order);
    }

    @Transactional
    public void updateInvoiceStatus(org.jfantasy.invoice.bean.enums.InvoiceStatus status, String... ids) {
        InvoiceStatus invoiceStatus = InvoiceStatus.submitted;
        switch (status) {
            case IN_PROGRESS:
                invoiceStatus = InvoiceStatus.processed;
                break;
            case COMPLETE:
                invoiceStatus = InvoiceStatus.completed;
                break;
            default:
        }
        for (String id : ids) {
            Order order = this.orderDao.get(id);
            order.setInvoiceStatus(invoiceStatus);
            this.orderDao.save(order);
        }
    }



    /**
     * 新订单
     *
     * @param details OrderDetails
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order submitOrder(OrderDTO details) {
        Long memberId = details.getMemberId();
        Long deliveryTypeId = details.getDeliveryTypeId();
        Long receiverId = details.getReceiverId();
        List<OrderPayeeDTO> payees = details.getPayees();
        List<OrderPriceDTO> prices = details.getPrices();
        String memo = details.getMemo();
        List<OrderItemDTO> items = details.getItems();

        Order order = new Order();
        // 初始订单相关状态
        order.setStatus(OrderStatus.unpaid);// 初始订单状态
        order.setPaymentStatus(PaymentStatus.unpaid);// 初始支付状态
        order.setShippingStatus(ShippingStatus.unshipped);// 初始发货状态
        order.setFlow(OrderFlow.initial);
        // 设置订单类型
        order.setType(details.getType());
        // 设置订单 target
        order.setDetailsId(details.getSn());
        order.setDetailsType(details.getType());
        // 订单所属人
        order.setMemberId(memberId);
        // 订单扩展字段
        order.setAttrs(details.getAttrs());
        // 收款方
        order.setPayee(-1L);
        // 初始化收货人信息
        if (receiverId != null) {
            Receiver receiver = receiverService.get(receiverId);
            order.setShipName(receiver.getName());// 收货人姓名
            order.setShipArea(receiver.getArea());// 收货地区存储
            order.setShipAddress(receiver.getAddress());// 收货地址
            order.setShipZipCode(receiver.getZipCode());// 收货邮编
            order.setShipMobile(receiver.getMobile());// 收货手机
        }
        if (!items.isEmpty()) {// 有订单项
            // 初始化订单项信息
            BigDecimal totalProductPrice = BigDecimal.ZERO;// 订单商品总价
            int totalProductQuantity = 0;// 订单商品数量
            int totalProductWeight = 0;// 订单商品总重量
            for (OrderItemDTO dto : items) {
                OrderItem item = new OrderItem();
                item.initialize(dto);
                totalProductPrice = totalProductPrice.add(item.getSubtotalPrice());
                totalProductQuantity += item.getProductQuantity();
                totalProductWeight += item.getSubtotalWeight();
                order.addItems(item);
            }
            order.setTotalProductWeight(totalProductWeight);
            order.setTotalProductQuantity(totalProductQuantity);
            order.setTotalProductPrice(totalProductPrice);
        } else {// 无订单项
            order.setTotalProductWeight(0);
            order.setTotalProductQuantity(0);
            order.setTotalProductPrice(BigDecimal.ZERO);
        }
        // 初始化配置信息
        if (deliveryTypeId != null) {
            DeliveryType deliveryType = deliveryTypeService.get(deliveryTypeId);
            BigDecimal deliveryFee = deliveryType.getFirstWeightPrice();
            if (order.getTotalProductWeight() > deliveryType.getFirstWeight() && deliveryType.getContinueWeightPrice().intValue() > 0) {// 如果订单重量大于配送首重量且配送方式续重方式价格大于0时,计算快递费
                Integer weigth = order.getTotalProductWeight() - deliveryType.getFirstWeight();
                Integer number;
                if (weigth % deliveryType.getContinueWeight() == 0) {
                    number = weigth / deliveryType.getContinueWeight();
                } else {
                    number = weigth / deliveryType.getContinueWeight() + 1;
                }
                deliveryFee = deliveryFee.add(deliveryType.getContinueWeightPrice().multiply(BigDecimal.valueOf(number)));
            }
            // 快递费用
            order.setDeliveryTypeId(deliveryType.getId());
            order.setDeliveryTypeName(deliveryType.getName());
            order.setDeliveryAmount(deliveryFee);
        } else {
            order.setDeliveryTypeId(null);
            order.setDeliveryTypeName(null);
            order.setDeliveryAmount(BigDecimal.ZERO);
        }
        // 初始化支付信息
        order.setPaidAmount(BigDecimal.ZERO);// 已付金额
        //解决附言不能为空的问题
        if (StringUtil.isNotBlank(memo)) {
            order.setMemo(memo);
        }
        // 产品价格 + 配送费
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 价格条目
        for (OrderPrice price : this.orderTypeService.prices(order.getType())) {
            OrderPriceDTO dto = ObjectUtil.find(prices, "code", price.getCode());
            if (dto == null) {
                throw new ValidationException(String.format("缺少关于%s的费用配置", price.getTitle()));
            }
            totalAmount = totalAmount.add(dto.getValue());
            order.addPrice(price, dto.getValue());
        }
        // 收款人条目
        for (OrderPayee payee : this.orderTypeService.payees(order.getType())) {
            if (payee.getType() == PayeeType.fixed) {
                continue;
            }
            OrderPayeeDTO dto = ObjectUtil.find(payees, "code", payee.getCode());
            if (dto == null) {
                throw new ValidationException(String.format("缺少关于%s的收款人配置", payee.getTitle()));
            }
            order.addPayee(payee, dto.getName(), dto.getValue(), dto.getTarget());
        }
        order.setTotalAmount(totalAmount);// 订单总金额(商品金额+邮费)
        //如果有优惠，应该在这里计算。
        order.setPayableAmount(order.getTotalAmount());//订单支付金额

        if (NumberUtil.isEquals(BigDecimal.ZERO, order.getTotalAmount())) {// 0 元订单，直接标记支付成功
            order.setStatus(OrderStatus.paid);
            order.setPaymentStatus(PaymentStatus.paid);
            order.setPaymentTime(DateUtil.now());
            order.setPaymentConfig(null);
            order.setPayConfigName("");
            // 查询付款人信息
            order.setPayer(order.getMemberId());
        }

        return this.orderDao.save(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ProfitChain> cashflow(String id) {
        Order order = this.orderDao.get(id);
        if (NumberUtil.isEquals(BigDecimal.ZERO, order.getTotalAmount())) {
            order.setProfitChains(Collections.emptyList());
            order.setPaymentStatus(PaymentStatus.archived);
            order.setFlow(OrderFlow.carveup);
            this.orderDao.update(order);
            return order.getProfitChains();
        }
        List<ProfitChain> profitChains = new ArrayList<>();
        if (order.getPaymentStatus() == PaymentStatus.paid) {
            Account platform = transactionService.platform();
            List<OrderCashFlow> cashFlows = orderTypeService.cashflows(order.getType(), Stage.finished);
            // 设置初始值
            order.setTotal(order.getTotalAmount());
            order.setSurplus(order.getTotalAmount());
            for (OrderCashFlow cashFlow : cashFlows) {
                BigDecimal surplus = order.getSurplus();
                order.setTotal(order.getTotalAmount());
                order.setSurplus(surplus.subtract(startupFlow(cashFlow, order, platform.getSn())));
                if (cashFlow.getProfitChain().getRevenue() != null) {
                    profitChains.add(cashFlow.getProfitChain());
                }
            }
            order.setProfitChains(profitChains);
            order.setPaymentStatus(PaymentStatus.archived);
            order.setFlow(OrderFlow.carveup);
            this.orderDao.update(order);
        }
        return order.getProfitChains();
    }

    /**
     * 启动流程
     *
     * @param cashFlow 订单现金流
     * @param order    订单
     * @param from     转出账户
     * @return BigDecimal
     */
    private BigDecimal startupFlow(OrderCashFlow cashFlow, Order order, String from) {
        String payee = cashFlow.getPayee(order);
        BigDecimal value = cashFlow.getValue(order);
        if (NumberUtil.isEquals(BigDecimal.ZERO, value)) {
            LOG.error(" 金额为 0.00，跳过分配规则 > " + cashFlow.getId());
            return value;
        }

        ProfitChain profitChain = cashFlow.getProfitChain(order);// logs

        Transaction transaction = transfer(order, cashFlow, value, from, payee, order.getId() + "->" + cashFlow.getCode(), cashFlow.getName());

        profitChain.setTradeNo(transaction.getSn());// logs

        // 设置初始值
        order.setTotal(transaction.getAmount());
        order.setSurplus(transaction.getAmount());
        for (OrderCashFlow flow : cashFlow.getSubflows()) {
            BigDecimal surplus = order.getSurplus();
            order.setTotal(transaction.getAmount());
            order.setSurplus(surplus.subtract(startupFlow(flow, order, payee)));

            profitChain.addChild(flow.getProfitChain());// logs
        }
        return transaction.getAmount();
    }

    /**
     * 转账接口
     *
     * @param order    订单对象
     * @param cashFlow 转账项目
     * @param amount   转账金额
     * @param from     转出账户
     * @param to       转入账户
     * @param unionKey 唯一标示
     * @param notes    备注
     * @return Transaction
     */
    private Transaction transfer(Order order, OrderCashFlow cashFlow, BigDecimal amount, String from, String to, String unionKey, String notes) {
        Map<String, Object> data = new HashMap<>();
        data.putAll(order.getAttrs());
        data.put(Transaction.UNION_KEY, unionKey);
        data.put(Transaction.ORDER_ID, order.getId());
        data.put(Transaction.ORDER_TYPE, order.getType());
        data.put("cashFlow_id", cashFlow.getId());
        data.put("cashFlow_stage", cashFlow.getStage());
        return transactionService.syncSave(cashFlow.getProject(), from, to, amount, notes, data);
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Autowired
    public void setDeliveryTypeService(DeliveryTypeService deliveryTypeService) {
        this.deliveryTypeService = deliveryTypeService;
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setReceiverService(ReceiverService receiverService) {
        this.receiverService = receiverService;
    }

    @Autowired
    public void setOrderTypeService(OrderTypeService orderTypeService) {
        this.orderTypeService = orderTypeService;
    }

}