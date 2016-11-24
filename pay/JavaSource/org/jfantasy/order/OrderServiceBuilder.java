package org.jfantasy.order;


import org.jfantasy.order.entity.enums.CallType;

import java.util.Map;

public interface OrderServiceBuilder<T extends OrderService> {

    CallType getCallType();

    T build(Map props);

}
