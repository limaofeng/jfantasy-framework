package org.jfantasy.pay.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.pay.bean.Order;
import org.jfantasy.pay.bean.OrderType;
import org.jfantasy.pay.event.OrderStatusChangedEvent;
import org.jfantasy.pay.job.OrderClose;
import org.jfantasy.pay.order.entity.enums.OrderStatus;
import org.jfantasy.pay.service.OrderTypeService;
import org.jfantasy.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderInsertOrUpdateListener extends AbstractChangedListener<Order> {

    private ScheduleService scheduleService;
    private OrderTypeService orderTypeService;

    public OrderInsertOrUpdateListener() {
        super(EventType.POST_COMMIT_INSERT,EventType.POST_COMMIT_UPDATE);
    }

    @Override
    protected void onPostInsert(Order entity, PostInsertEvent event) {
        OrderType orderType = getOrderTypeService().get(entity.getType());
        Map<String, String> data = new HashMap<>();
        data.put("id", entity.getKey());
        this.scheduleService.addTrigger(OrderClose.JOB_KEY, OrderClose.triggerKey(entity), DateUtil.format(orderType.getExpires(), "ss mm HH dd MM ? yyyy"), data);
    }

    @Override
    protected void onPostUpdate(Order entity, PostUpdateEvent event) {
        if (modify(event, "status")) {
            if (entity.getStatus() == OrderStatus.paid) {
                this.scheduleService.removeTrigdger(OrderClose.triggerKey(entity));
            }
            this.applicationContext.publishEvent(new OrderStatusChangedEvent(entity));
        }
    }

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    private OrderTypeService getOrderTypeService() {
        if (orderTypeService == null) {
            orderTypeService = this.applicationContext.getBean(OrderTypeService.class);
        }
        return this.orderTypeService;
    }

}
