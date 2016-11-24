package org.jfantasy.order;

import org.jfantasy.order.entity.Order;
import org.jfantasy.order.entity.enums.OrderStatus;

public interface OrderMessageListener {

    void on(String type, String sn, OrderStatus status, Order details);

}
