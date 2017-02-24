package org.jfantasy.order.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.event.OrderFlowEvent;
import org.jfantasy.order.event.OrderStatusEvent;
import org.jfantasy.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderInsertOrUpdateListener extends AbstractChangedListener<Order> {

    private transient ScheduleService scheduleService;

    public OrderInsertOrUpdateListener() {
        super(EventType.POST_COMMIT_INSERT, EventType.POST_COMMIT_UPDATE);
    }

    @Override
    protected void onPostInsert(Order entity, PostInsertEvent event) {
        this.applicationContext.publishEvent(OrderStatusEvent.newInstance(entity));
    }

    @Override
    protected void onPostUpdate(Order entity, PostUpdateEvent event) {
        if (modify(event, "status")) {
            this.applicationContext.publishEvent(OrderStatusEvent.newInstance(entity));
        }
        if (modify(event, "flow")) {
            this.applicationContext.publishEvent(OrderFlowEvent.newInstance(entity));
        }
    }

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

}
