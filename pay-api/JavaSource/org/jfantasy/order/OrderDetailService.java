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
     * @param type 业务订单 type
     * @param sn   业务订单 sn
     * @return Order
     */
    OrderDTO get(String type, String sn);

    /**
     * 订单退款
     *
     * @param type         业务订单 type
     * @param sn           业务订单 sn
     * @param refundAmount 退款金额
     * @param note         备注
     * @return Order
     */
    OrderDTO refund(String type, String sn, BigDecimal refundAmount, String note);


    /**
     * 发起订单关闭操作
     *
     * @param type 业务订单 type
     * @param sn   业务订单 sn
     */
    void close(String type, String sn);

}
