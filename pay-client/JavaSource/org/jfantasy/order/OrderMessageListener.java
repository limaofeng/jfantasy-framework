package org.jfantasy.order;

import org.jfantasy.order.entity.OrderDTO;
import org.jfantasy.order.entity.enums.OrderStatus;

public interface OrderMessageListener {

    void on(String type, String sn, OrderStatus status, OrderDTO details);

}
