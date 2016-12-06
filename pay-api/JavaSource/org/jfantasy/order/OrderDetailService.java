package org.jfantasy.order;


import org.jfantasy.order.entity.OrderDTO;

import java.math.BigDecimal;

/**
 * 支付订单接口<br/>
 * 服务接入方需要实现该接口
 */
public interface OrderDetailService {

    /**
     * 保存业务订单到交易系统
     *
     * @param order 业务订单
     */
    OrderDTO save(OrderDTO order);

    /**
     * 查询交易订单信息
     *
     * @param id 订单ID
     * @return Order
     */
    OrderDTO get(String id);

    /**
     * 订单退款
     *
     * @param id           订单ID
     * @param refundAmount 退款金额
     * @param note         备注
     * @return Order
     */
    OrderDTO refund(String id, BigDecimal refundAmount, String note);


    /**
     * 关闭订单
     *
     * @param id 订单ID
     */
    void close(String id);

    /**
     * 完成订单
     *
     * @param id    订单ID
     */
    void complete(String id);

}
