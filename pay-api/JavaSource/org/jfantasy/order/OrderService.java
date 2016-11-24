package org.jfantasy.order;


import org.jfantasy.order.entity.Order;

/**
 * 支付订单接口<br/>
 * 服务接入方需要实现该接口
 */
public interface OrderService {

    /**
     * 保存业务订单到交易系统
     *
     * @param order 业务订单
     */
    void save(Order order);

    /**
     * 查询交易订单信息
     *
     * @param type 业务订单 type
     * @param sn   业务订单 sn
     * @return Order
     */
    Order get(String type, String sn);

    /**
     * 发起订单关闭操作
     *
     * @param type 业务订单 type
     * @param sn   业务订单 sn
     */
    void close(String type, String sn);

}
