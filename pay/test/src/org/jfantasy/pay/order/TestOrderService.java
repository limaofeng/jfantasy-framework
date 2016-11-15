package org.jfantasy.pay.order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.pay.order.entity.*;
import org.jfantasy.pay.order.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TestOrderService implements OrderService {

    protected final Log LOG = LogFactory.getLog(TestOrderService.class);

    @Override
    public String[] types() {
        return new String[]{"test"};
    }

    @Override
    public OrderDetails loadOrder(final OrderKey key) {
        OrderDetails order = new OrderDetails();
        order.setSn(key.getSn());
        order.setType(key.getType());
        order.setSubject("测试订单");
        order.setBody("商品详情");
        order.setTotalFee(BigDecimal.valueOf(0.01));
        order.setPayableFee(BigDecimal.valueOf(0.01));
        order.setOrderItems(new ArrayList<OrderItem>() {
            {
                OrderItem item = new OrderItem();
                item.setSn("SN000001");
                item.setName("这个是测试订单项");
                item.setQuantity(1);
                this.add(item);
            }
        });
        return order;
    }

    @Override
    public void on(OrderKey key, OrderStatus status, OrderDetails details) {
        System.out.println(key + " - " + status + " - " + details);
    }

}