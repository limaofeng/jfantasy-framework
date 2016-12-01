package org.jfantasy.trade.event;

import org.jfantasy.trade.bean.Point;
import org.springframework.context.ApplicationEvent;

public class PointChangedEvent extends ApplicationEvent {

    public PointChangedEvent(Point point) {
        super(point);
    }

    public Point getPoint() {
        return (Point) this.getSource();
    }

}