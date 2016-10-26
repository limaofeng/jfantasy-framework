package org.jfantasy.pay.order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.pay.order.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestOrderService implements OrderService {

    protected static final Log LOG = LogFactory.getLog(TestOrderService.class);

    public static final String ORDER_TYPE = "test";

    private static TestOrderService instance = new TestOrderService();

    private TestOrderService(){
    }

    public static TestOrderService getInstance(){
        return instance;
    }

    @Override
    public String[] types() {
        return new String[]{ORDER_TYPE};
    }

    @Override
    public OrderDetails loadOrder(final OrderKey sn) {
        OrderDetails order = new OrderDetails();
        order.setType(ORDER_TYPE);
        order.setSubject("测试订单");
        order.setBody("商品详情");
        order.setTotalFee(BigDecimal.valueOf(0.01));
        order.setPayableFee(BigDecimal.valueOf(0.01));
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setSn("SN000001");
        item.setName("这个是测试订单项");
        item.setQuantity(1);
        orderItems.add(item);
        order.setOrderItems(orderItems);
        return order;
    }

    @Override
    public void on(OrderKey key, PaymentDetails status, String message) {
        LOG.debug(key + " - " + status + " - " + message);
    }

    @Override
    public void on(OrderKey key, RefundDetails status, String message) {
        LOG.debug(key + " - " + status + " - " + message);
    }

}
