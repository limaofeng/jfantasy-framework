package org.jfantasy.order.event;

public class OrderNewEvent extends OrderStatusEvent {

    public OrderNewEvent(String id) {
        super(id);
    }

}
