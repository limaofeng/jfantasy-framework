package org.jfantasy.logistics.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.logistics.bean.DeliveryItem;
import org.jfantasy.logistics.bean.DeliveryType;
import org.jfantasy.logistics.bean.Logistics;
import org.jfantasy.logistics.dao.DeliveryItemDao;
import org.jfantasy.logistics.dao.DeliveryTypeDao;
import org.jfantasy.logistics.dao.LogisticsDao;
import org.jfantasy.order.bean.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LogisticsService {

    private final LogisticsDao logisticsDao;
    private final DeliveryTypeDao deliveryTypeDao;
    private final DeliveryItemDao deliveryItemDao;
//    @Autowired
//    private OrderServiceFactory orderServiceFactory;
    private final ApplicationContext applicationContext;

    @Autowired
    public LogisticsService(LogisticsDao logisticsDao, DeliveryTypeDao deliveryTypeDao, DeliveryItemDao deliveryItemDao, ApplicationContext applicationContext) {
        this.logisticsDao = logisticsDao;
        this.deliveryTypeDao = deliveryTypeDao;
        this.deliveryItemDao = deliveryItemDao;
        this.applicationContext = applicationContext;
    }

    public Pager<Logistics> findPager(Pager<Logistics> pager, List<PropertyFilter> filters) {
        return this.logisticsDao.findPager(pager, filters);
    }

    public Order getOrder(String id) {
        Logistics logistics = this.logisticsDao.get(id);
        //OrderService orderDetailsService = orderServiceFactory.getOrderService(shipping.getOrderType());
        return null;//orderDetailsService.loadOrderBySn(shipping.getOrderSn());
    }

    /**
     * 发货信息
     *
     * @param deliveryTypeId 配送方式
     * @param orderId        订单SN
     * @param items          物流项
     * @return Logistics
     */
    public Logistics save(Long deliveryTypeId, String orderId, List<DeliveryItem> items) {
        //初始化发货信息
        Logistics shipping = new Logistics();
        shipping.setOrderId(orderId);
        shipping.setDeliveryType(new DeliveryType(deliveryTypeId));
        shipping.setDeliveryItems(items);
        //获取订单信息
//        OrderService orderDetailsService = orderServiceFactory.getOrderService(orderType);
//        Order order = orderDetailsService.loadOrderBySn(orderSn);
        DeliveryType deliveryType = deliveryTypeDao.get(deliveryTypeId);
        shipping.setDeliveryType(deliveryType);
        // 初始化快递信息
        shipping.setDeliveryTypeName(deliveryType.getName());
        shipping.setDeliveryCorpName(deliveryType.getExpress().getName());
        shipping.setDeliveryCorpUrl(deliveryType.getExpress().getUrl());
        // 添加收货地址信息
//        shipping.setShipName(order.getShipAddress().getName());
//        shipping.setShipArea(order.getShipAddress().getArea());
//        shipping.setShipAddress(order.getShipAddress().getAddress());
//        shipping.setShipZipCode(order.getShipAddress().getZipCode());
//        shipping.setShipMobile(order.getShipAddress().getMobile());

        shipping = this.logisticsDao.save(shipping);

        // 初始化物流项
//        for (DeliveryItem item : shipping.getDeliveryItems()) {
//            item.initialize(ObjectUtil.find(order.getOrderItems(), "sn", item.getSn()));
//            item.setLogistics(shipping);
//            this.deliveryItemDao.update(item);
//        }
//        applicationContext.publishEvent(new LogisticsEvent(shipping, order));
        return shipping;
    }

    public Logistics get(String id) {
        return this.logisticsDao.get(id);
    }

    public void delete(String... ids) {
        for (String id : ids) {
            this.logisticsDao.delete(id);
        }
    }

}
