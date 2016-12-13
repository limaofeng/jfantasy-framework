package org.jfantasy.invoice.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.invoice.bean.Invoice;
import org.jfantasy.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发票状态监听
 */
@Component
public class InvoiceStatusChangeListener extends AbstractChangedListener<Invoice> {

    private transient OrderService orderService;

    public InvoiceStatusChangeListener() {
        super(EventType.POST_UPDATE);
    }

    @Override
    protected void onPostUpdate(Invoice entity, PostUpdateEvent event) {
        if (modify(event, "status")) {
            String[] orderIds = ObjectUtil.toFieldArray(entity.getItems(), "order.id", String.class);
            orderService.updateInvoiceStatus(entity.getStatus(), orderIds);
        }
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

}
