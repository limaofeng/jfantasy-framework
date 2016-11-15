package org.jfantasy.pay.order;


import org.jfantasy.pay.order.entity.OrderDetails;
import org.jfantasy.pay.order.entity.OrderKey;
import org.jfantasy.pay.order.entity.enums.OrderStatus;

/**
 * 支付订单接口<br/>
 * 服务接入方需要实现该接口
 */
public interface OrderService {

    /**
     * 订单类型
     *
     * @return String
     */
    String[] types();

    /**
     * 查询订单信息
     *
     * @param key 订单Key <br/>
     *            格式为: "type:sn" <br/>
     *            样例: "test:t000001"
     * @return OrderDetails
     */
    OrderDetails loadOrder(OrderKey key);

    /**
     * 支付事件
     *
     * @param key     KEY
     * @param status  状态
     * @param details 详情
     */
    void on(OrderKey key, OrderStatus status, OrderDetails details);

}
