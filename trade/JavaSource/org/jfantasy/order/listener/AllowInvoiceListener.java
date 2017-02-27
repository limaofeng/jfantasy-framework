package org.jfantasy.order.listener;

import org.jfantasy.framework.util.common.NumberUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.event.OrderCarveupEvent;
import org.jfantasy.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class AllowInvoiceListener implements ApplicationListener<OrderCarveupEvent> {

    private OrderService orderService;

    @Override
    @Transactional
    public void onApplicationEvent(OrderCarveupEvent event) {
        Order order = orderService.get(event.getOrderId());
        // 更新发票状态
        if (!NumberUtil.isEquals(BigDecimal.ZERO, order.getTotalAmount()) && !"walletpay".equals(order.getPaymentConfig().getPayProductId())) {// 非钱包支付，可以开发票
            orderService.updateAllowInvoice(order.getId());
        }
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

}
