package org.jfantasy.invoice.listener;

import org.jfantasy.order.event.OrderCompleteEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 订单处理完成后，的发票开具操作
 */
@Component
public class InvoiceOrderCompleteListener implements ApplicationListener<OrderCompleteEvent> {

    @Override
    public void onApplicationEvent(OrderCompleteEvent event) {

    }

}
