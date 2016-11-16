package org.jfantasy.pay.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.pay.bean.Point;
import org.jfantasy.pay.event.PointChangedEvent;
import org.springframework.stereotype.Component;

@Component
public class PointChangedListener extends AbstractChangedListener<Point> {

    private static final long serialVersionUID = -6602686718813126066L;

    public PointChangedListener(){
        super(EventType.POST_COMMIT_INSERT,EventType.POST_COMMIT_UPDATE);
    }

    @Override
    public void onPostInsert(Point point,PostInsertEvent event) {
        if (missing(event)) {
            return;
        }
        applicationContext.publishEvent(new PointChangedEvent(point));
    }

    @Override
    public void onPostUpdate(Point point,PostUpdateEvent event) {
        if (modify(event, "status")) {
            applicationContext.publishEvent(new PointChangedEvent(point));
        }
    }

}
