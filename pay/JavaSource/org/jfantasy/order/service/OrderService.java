package org.jfantasy.order.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.logistics.bean.DeliveryType;
import org.jfantasy.logistics.service.DeliveryTypeService;
import org.jfantasy.order.OrderDetailService;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderItem;
import org.jfantasy.order.bean.OrderTargetKey;
import org.jfantasy.order.bean.OrderType;
import org.jfantasy.order.entity.enums.PaymentStatus;
import org.jfantasy.order.entity.enums.ShippingStatus;
import org.jfantasy.order.dao.OrderDao;
import org.jfantasy.order.dao.OrderItemDao;
import org.jfantasy.order.dao.OrderTypeDao;
import org.jfantasy.order.entity.OrderDTO;
import org.jfantasy.order.entity.OrderItemDTO;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.job.OrderClose;
import org.jfantasy.order.service.vo.Receiver;
import org.jfantasy.rpc.annotation.ServiceExporter;
import org.jfantasy.schedule.service.ScheduleService;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单明细记录
 */
@ServiceExporter(value = "orderService", targetInterface = OrderDetailService.class)
public class OrderService implements OrderDetailService {

    private static final Log LOG = LogFactory.getLog(OrderService.class);

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final OrderTypeDao orderTypeDao;
    private TransactionService transactionService;
    private ApiGatewaySettings apiGatewaySettings;
    private ScheduleService scheduleService;
    private DeliveryTypeService deliveryTypeService;

    @Autowired
    public OrderService(OrderTypeDao orderTypeDao, OrderDao orderDao, OrderItemDao orderItemDao) {
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Order get(String id) {
        return this.orderDao.get(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Pager<Order> findPager(Pager<Order> pager, List<PropertyFilter> filters) {
        return this.orderDao.findPager(pager, filters);
    }

    @Transactional
    public void update(Order order) {
        this.orderDao.update(order);
    }

    @Transactional
    public Order close(String id) {
        Order order = this.orderDao.get(id);
        if (OrderStatus.unpaid != order.getStatus()) {
            throw new ValidationException("order = [" + id + "] 订单已经支付，不能关闭!");
        }
        // 确认第三方支付成功后，修改关闭状态
        Transaction transaction = this.transactionService.getByUniqueId(Transaction.generateUnionid(Project.PAYMENT, order.getId()));
        if(transaction != null) {
            transaction.setStatus(TxStatus.close);
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
    public Order get(OrderTargetKey key) {
        return findUnique(key.getType(), key.getSn());
    }

    @Transactional
    public Order findUnique(String targetType, String targetId) {
        return this.orderDao.findUnique(Restrictions.eq("detailsType", targetType), Restrictions.eq("detailsId", targetId));
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
        String memo = details.getMemo();
        List<OrderItemDTO> items = details.getItems();

        Order order = new Order();
        // 初始订单相关状态
        order.setStatus(OrderStatus.unpaid);// 初始订单状态
        order.setPaymentStatus(PaymentStatus.unpaid);// 初始支付状态
        order.setShippingStatus(ShippingStatus.unshipped);// 初始发货状态
        // 设置订单类型
        order.setType(details.getType());
        // 设置订单 target
        order.setDetailsId(details.getSn());
        order.setDetailsType(details.getType());
        // 订单所属人
        order.setMemberId(memberId);
        // 订单扩展字段
        order.setAttrs(details.getAttrs());
        // 初始化收货人信息
        if (receiverId != null) {
            Receiver receiver = getReceiverById(receiverId);
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
        BigDecimal totalAmount = order.getTotalProductPrice().add(order.getDeliveryAmount());
        //额外的价格条目
        /*
        for(PriceVO vo : priceVOS){
            OrderPrice price = new OrderPrice();
            price.setCode(vo.getCode());
            price.setName(vo.getName());
            price.setAmount(vo.getAmount());
            price.setOrder(order);
            this.orderPriceDao.save(price);
            totalAmount = totalAmount.add(vo.getAmount());
        }
        */
        order.setTotalAmount(totalAmount);// 订单总金额(商品金额+邮费)
        //如果有优惠，应该在这里计算。
        order.setPayableAmount(order.getTotalAmount());//订单支付金额
        this.orderDao.save(order);
        for (OrderItem item : order.getItems()) {
            this.orderItemDao.save(item);
        }
        return order;
    }

    @Override
    @Transactional
    public OrderDTO save(OrderDTO zorder) {
        Order order = this.findUnique(zorder.getType(), zorder.getSn());
        if (order != null) {
            zorder.setId(order.getId());
            return zorder;
        }
        OrderType orderType = orderTypeDao.get(zorder.getType());
        if (orderType == null || !orderType.getEnabled()) {
            throw new ValidationException("支付系统不能处理该类型的订单[" + zorder.getType() + "]，请检查或者联系开发人员!");
        }
        //保存订单信息
        order = this.submitOrder(zorder);
        zorder.setId(order.getId());
        return zorder;
    }

    @Override
    public OrderDTO get(String type, String sn) {
        return null;
    }

    @Override
    public OrderDTO refund(String type, String sn, BigDecimal refundAmount, String note) {
        return null;
    }

    @Override
    public void close(String type, String sn) {

    }

    private Receiver getReceiverById(Long id) {
        try {
            HttpResponse<Receiver> response = Unirest.get(apiGatewaySettings.getUrl() + "/receivers/" + id).asObject(Receiver.class);
            return response.getBody();
        } catch (UnirestException e) {
            LOG.error(e.getMessage(), e);
            throw new RestException("读取地址信息出错!");
        }
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
    public void setApiGatewaySettings(ApiGatewaySettings apiGatewaySettings) {
        this.apiGatewaySettings = apiGatewaySettings;
    }

}