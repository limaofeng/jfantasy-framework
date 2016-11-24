package org.jfantasy.order.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.order.OrderServiceBuilder;
import org.jfantasy.order.bean.OrderServer;
import org.jfantasy.order.OrderServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderServerChangedListener extends AbstractChangedListener<OrderServer> {

    private static final long serialVersionUID = 6697184909285106945L;

    private OrderServiceBuilder builder;

    private OrderServiceFactory orderServiceFactory;

    public OrderServerChangedListener() {
        super(EventType.POST_COMMIT_INSERT,EventType.POST_COMMIT_UPDATE);
    }

    @Override
    public void onPostInsert(OrderServer entity, PostInsertEvent event) {
        if (entity.isEnabled()) {
            orderServiceFactory.register(entity.getType(), builder.build(entity.getProperties()));
        }
    }

    @Override
    public void onPostUpdate(OrderServer entity, PostUpdateEvent event) {
        if (modify(event, "enabled")) {
            if (entity.isEnabled()) {
                orderServiceFactory.register(entity.getType(), builder.build(entity.getProperties()));
            } else {
                orderServiceFactory.unregister(entity.getType());
            }
        }
    }

    @Autowired
    public void setOrderServiceFactory(OrderServiceFactory orderServiceFactory) {
        this.orderServiceFactory = orderServiceFactory;
    }

    @Autowired
    public void setBuilder(OrderServiceBuilder builder) {
        this.builder = builder;
    }

}
